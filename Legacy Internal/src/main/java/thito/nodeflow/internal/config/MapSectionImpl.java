package thito.nodeflow.internal.config;

import thito.nodeflow.api.config.*;

import java.util.*;
import java.util.stream.*;

public class MapSectionImpl implements MapSection {

    protected Map<String, Object> map = new LinkedHashMap<>();
    public MapSectionImpl() {
    }

    public MapSectionImpl(Map<?, ?> map) {
        for (Entry<?,?> entry : map.entrySet()) {
            Object key = entry.getKey();
            if (key instanceof String) {
                Object value = entry.getValue();
                if (value instanceof Map) {
                    value = Section.wrapValue(value);
                }
                put((String) key, value);
            }
        }
    }

    @Override
    public Collection<Object> keys() {
        return map.keySet().stream().map(x -> (Object)x).collect(Collectors.toSet());
    }

    @Override
    public boolean isMap() {
        return true;
    }

    @Override
    public boolean isList() {
        return false;
    }

    @Override
    public MapEditor<MapSection, String, Object> edit() {
        return MapEditor.newEditor(this);
    }

    @Override
    public Object unwrap() {
        return map;
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        for (Object object : map.values()) {
            if (Section.equals(object, value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object get(Object key) {
        return map.get(key);
    }

    @Override
    public Object put(String key, Object value) {
        return map.put(key, Section.wrapValue(value));
    }

    @Override
    public Object remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        for (Entry<? extends String, ?> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<String> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<Object> values() {
        return map.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return map.entrySet();
    }

    @Override
    public Object getScope(Object key) {
        if (key != null) {
            return get(String.valueOf(key));
        }
        return null;
    }

    @Override
    public void setScope(Object key, Object value) {
        put(String.valueOf(key), value);
    }

    @Override
    public String toString() {
        return String.valueOf(map);
    }
}
