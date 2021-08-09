package thito.nodeflow.internal.config;

import thito.nodeflow.api.config.*;

import java.util.*;

public class MapEditorImpl<M extends Map<K, V>, K, V> implements MapEditor<M, K, V> {
    private M map;

    public MapEditorImpl(M map) {
        this.map = map;
    }

    @Override
    public MapEditor<M, K, V> put(K key, V value) {
        map.put(key, value);
        return this;
    }

    @Override
    public MapEditor<M, K, V> remove(Object key) {
        map.remove(key);
        return this;
    }

    @Override
    public MapEditor<M, K, V> remove(Object key, Object value) {
        map.remove(key, value);
        return this;
    }

    @Override
    public MapEditor<M, K, V> putAll(Map<? extends K, ? extends V> map) {
        this.map.putAll(map);
        return this;
    }

    @Override
    public MapEditor<M, K, V> clear() {
        map.clear();
        return this;
    }

    @Override
    public M done() {
        return map;
    }
}
