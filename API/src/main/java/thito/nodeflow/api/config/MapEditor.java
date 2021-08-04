package thito.nodeflow.api.config;

import thito.nodeflow.api.NodeFlow;

import java.util.Map;

public interface MapEditor<M extends Map<K, V>, K, V> {
    static <M extends Map<K, V>, K, V> MapEditor<M, K, V> newEditor(M map) {
        return NodeFlow.getApplication().getToolkit().newMapEditor(map);
    }

    MapEditor<M, K, V> put(K key, V value);

    MapEditor<M, K, V> remove(Object key);

    MapEditor<M, K, V> remove(Object key, Object value);

    MapEditor<M, K, V> putAll(Map<? extends K, ? extends V> map);

    default MapEditor<M, K, V> removeAll(K... keys) {
        for (K key : keys) remove(key);
        return this;
    }

    default MapEditor<M, K, V> removeAll(Iterable<K> keys) {
        for (K key : keys) remove(key);
        return this;
    }

    MapEditor<M, K, V> clear();

    default MapEditor<M, K, V> setAll(Map<? extends K, ? extends V> map) {
        clear();
        putAll(map);
        return this;
    }

    M done();
}
