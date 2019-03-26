package org.overbaard.review.tool.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple expiring cache, we should probably use a timer at some stage to evict entries
 * rather than relying on that being triggered by add/get/remove
 *
 * We don't want a LRU cache, as the time to remove an entry is from when we add the entry.
 *
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class ExpiryCache<K, V> {

    final int maxEntries;
    final long timeoutMilliSeconds;

    volatile long lastExpiryRun = System.currentTimeMillis();

    ConcurrentHashMap<K, CacheEntry<K, V>> map = new ConcurrentHashMap<>();


    public ExpiryCache(int maxEntries, int timeoutMinutes) {
        if (maxEntries != -1 && maxEntries < 10) {
            throw new IllegalStateException("Max entries must be -1 (unlimited) or at least 10");
        }
        this.maxEntries = maxEntries;
        this.timeoutMilliSeconds = timeoutMinutes * 60 * 1000;
    }


    public V add(K key, V value) {
        long now = System.currentTimeMillis();

        // Does cleanup
        V existing = get(key);

        if (map.size() > maxEntries) {
            cleanWhenMoreThanMaxEntries();
        }

        CacheEntry cacheEntry = new CacheEntry(key, value, now + timeoutMilliSeconds);
        map.put(key, cacheEntry);

        // No need to clean up here, as the get() call above does it
        return existing;
    }

    public V get(K key) {
        try {
            long now = System.currentTimeMillis();
            long expired = now - timeoutMilliSeconds;
            CacheEntry<K, V> cacheEntry = map.get(key);
            if (cacheEntry != null) {
                if (cacheEntry.expires < expired) {
                    map.remove(key);
                    return null;
                }
                return cacheEntry.value;
            }
        } finally {
            cleanExpiredEntries();
        }
        return null;
    }

    public V remove(K key) {
        try {
            long now = System.currentTimeMillis();
            long expired = now - timeoutMilliSeconds;
            CacheEntry<K, V> cacheEntry = map.remove(key);
            if (cacheEntry != null) {
                if (cacheEntry.expires < expired) {
                    return null;
                }
                return cacheEntry.value;
            }
        } finally {
            cleanExpiredEntries();
        }
        return null;
    }

    private void cleanExpiredEntries() {
        long now = System.currentTimeMillis();
        long nextExpiryRun = lastExpiryRun + timeoutMilliSeconds;
        if (now > nextExpiryRun) {
            synchronized (this) {
                if (now > nextExpiryRun) {
                    long expired = now - timeoutMilliSeconds;
                    for (CacheEntry<K, V> cacheEntry : map.values()) {
                        if (cacheEntry.expires < expired) {
                            map.remove(cacheEntry.key);
                        }
                    }
                    lastExpiryRun = now;
                }
            }
        }
    }

    private void cleanWhenMoreThanMaxEntries() {
        if (maxEntries == -1) {
            return;
        }
        if (map.size() > maxEntries) {
            synchronized (this) {
                if (map.size() > maxEntries) {
                    int targetSize = (int)(maxEntries * .8);
                    List<CacheEntry<K, V>> entries = new ArrayList<>(map.values());
                    entries.sort(Comparator.comparingLong(o -> o.expires));
                    int toRemove = entries.size() - targetSize;
                    for (int i = 0; i < toRemove; i++) {
                        map.remove(entries.get(i).key);
                    }
                }
            }
        }
    }

    private static class CacheEntry<K, V> {
        final K key;
        final V value;
        final long expires;

        public CacheEntry(K key, V value, long expires) {
            this.key = key;
            this.value = value;
            this.expires = expires;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public long getExpires() {
            return expires;
        }
    }

}
