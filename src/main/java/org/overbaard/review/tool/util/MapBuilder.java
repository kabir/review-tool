package org.overbaard.review.tool.util;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class MapBuilder<K, V> {
    private final Map<K, V> map;

    private MapBuilder(Map<K, V> map) {
        this.map = map;
    }

    public static <K, V> MapBuilder<K, V> linkedHashMap() {
        return new MapBuilder<>(new LinkedHashMap<>());
    }

    public MapBuilder<K, V> put(K key, V value) {
        map.put(key, value);
        return this;
    }

    public Map<K, V> build() {
        return Collections.unmodifiableMap(map);
    }
}
