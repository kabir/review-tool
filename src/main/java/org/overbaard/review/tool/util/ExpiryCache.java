package org.overbaard.review.tool.util;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple expiring cache, which has an eviction method to purge old entries triggered by a timer.
 *
 * We don't want a LRU cache, as the time to remove an entry is from when we add the entry rather than
 * when it was last accessed.
 *
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class ExpiryCache<K, V> {

    final long timeoutMilliSeconds;

    volatile long lastExpiryRun = System.currentTimeMillis();

    ConcurrentHashMap<K, CacheEntry<K, V>> map = new ConcurrentHashMap<>();


    public ExpiryCache(int timeoutSeconds) {
        this.timeoutMilliSeconds = timeoutSeconds * 1000;
    }


    public V add(K key, V value) {
        long now = System.currentTimeMillis();

        V existing = get(key);

        CacheEntry cacheEntry = new CacheEntry(key, value, now + timeoutMilliSeconds);
        map.put(key, cacheEntry);

        // No need to clean up here, as the get() call above does it
        return existing;
    }

    public V get(K key) {
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

        return null;
    }

    public V remove(K key) {
        long now = System.currentTimeMillis();
        long expired = now - timeoutMilliSeconds;
        CacheEntry<K, V> cacheEntry = map.remove(key);
        if (cacheEntry != null) {
            if (cacheEntry.expires < expired) {
                return null;
            }
            return cacheEntry.value;
        }
        return null;
    }

    public void evictExpiredEntries() {
        long now = System.currentTimeMillis();
        long expired = now - timeoutMilliSeconds;
        for (CacheEntry<K, V> cacheEntry : map.values()) {
            if (cacheEntry.expires < expired) {
                map.remove(cacheEntry.key);
            }
        }
        lastExpiryRun = now;
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
