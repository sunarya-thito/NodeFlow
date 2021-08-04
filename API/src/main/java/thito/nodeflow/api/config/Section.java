package thito.nodeflow.api.config;

import thito.nodeflow.api.NodeFlow;
import thito.nodeflow.api.ReportedError;
import thito.nodeflow.api.resource.ResourceFile;
import thito.nodeflow.api.resource.WritableResourceFile;

import java.io.Reader;
import java.io.Writer;
import java.util.*;

public interface Section {
    String STRING_DEFAULT_VALUE = "";
    Number NUMBER_DEFAULT_VALUE = 0;
    //    MapSection MAP_DEFAULT_VALUE = new MapSection() {
//        MapSection initializeDefault() {
//            map = Collections.unmodifiableMap(map);
//            return this;
//        }
//    }.initializeDefault();
//    ListSection LIST_DEFAULT_VALUE = new ListSection() {
//        ListSection initializeDefault() {
//            list = Collections.unmodifiableList(list);
//            return this;
//        }
//    }.initializeDefault();
    MapSection MAP_DEFAULT_VALUE = NodeFlow.getApplication() == null ? null : NodeFlow.getApplication().getToolkit().newDefaultMapSection();
    ListSection LIST_DEFAULT_VALUE = NodeFlow.getApplication() == null ? null : NodeFlow.getApplication().getToolkit().newDefaultListSection();

    static MapSection newMap() {
        return NodeFlow.getApplication().getToolkit().newMapSection();
    }

    static ListSection newList() {
        return NodeFlow.getApplication().getToolkit().newListSection();
    }

    static boolean equals(Object a, Object b) {
        if (a instanceof Section) {
            a = ((Section) a).unwrap();
        }
        if (b instanceof Section) {
            b = ((Section) b).unwrap();
        }
        return Objects.deepEquals(a, b);
    }

    static Section loadYaml(ResourceFile file) {
        try (Reader reader = file.openReader()) {
            return loadYaml(reader);
        } catch (Throwable t) {
            throw new ReportedError(t);
        }
    }

    static Section loadYaml(Reader reader) {
//        return (Section) wrapValue(new Yaml().loadAs(reader, Map.class));
        return NodeFlow.getApplication().getToolkit().loadYaml(reader);
    }

    static void saveYaml(Section section, WritableResourceFile file) {
        try (Writer writer = file.openWriter()) {
            saveYaml(section, writer);
        } catch (Throwable t) {
            throw new ReportedError(t);
        }
    }

    static void saveYaml(Section section, Writer writer) {
//        DumperOptions options = new DumperOptions();
//        String dump = new Yaml(options).dumpAsMap(section);
//        writer.write(dump);
        NodeFlow.getApplication().getToolkit().saveYaml(section, writer);
    }

    static Object wrapValue(Object value) {
        if (value == null) return null;
        if (value instanceof MapSection || value instanceof ListSection) return value;
        if (value instanceof Map) {
            return NodeFlow.getApplication().getToolkit().newMapSection((Map<?, ?>) value);
        }
        if (value instanceof List) {
            return NodeFlow.getApplication().getToolkit().newListSection((List<?>) value);
        }
        return value;
    }

    default Set<String> collectMapKeys(boolean deep) {
        Set<String> mapKeys = new HashSet<>();
        if (isMap()) {
            for (Map.Entry<String, Object> entry : asMap().entrySet()) {
                mapKeys.add(entry.getKey());
                if (entry.getValue() instanceof MapSection && deep) {
                    for (String key : ((MapSection) entry.getValue()).collectMapKeys(true)) {
                        mapKeys.add(entry.getKey()+"."+key);
                    }
                }
            }
        }
        return mapKeys;
    }

    default MapSection asMap() {
        return this instanceof MapSection ? (MapSection) this : MAP_DEFAULT_VALUE;
    }

    default ListSection asList() {
        return this instanceof ListSection ? (ListSection) this : LIST_DEFAULT_VALUE;
    }

    Object getScope(Object key);

    void setScope(Object key, Object value);

    default void setWithOffset(int start, Object value, Object... key) {
        Object current = key[start];
        if (start + 1 < key.length) {
            Object next = getScope(current);
            if (!(next instanceof Section)) {
                if (current == null) {
                    throw new NullPointerException();
                } else if (current instanceof Number) {
                    int targetIndex = ((Number) next).intValue();
                    setScope(current, next = NodeFlow.getApplication().getToolkit().newListSection(new Object[targetIndex + 1]));
                } else {
                    setScope(current, next = NodeFlow.getApplication().getToolkit().newMapSection());
                }
            }
            ((Section) next).setWithOffset(start + 1, value, key);
        } else {
            setScope(current, value);
        }
    }

    default void set(Object value, Object... key) {
        if (key.length > 0) setWithOffset(0, value, key);
    }

    default String getString(Object... key) {
        Object value = getObject(key);
        if (value != null) {
            return String.valueOf(value);
        }
        return STRING_DEFAULT_VALUE;
    }

    default boolean getBoolean(Object... key) {
        Object value = getObject(key);
        if (value == null) return false;
        if (value instanceof Boolean) {
            return (boolean) value;
        } else if (value instanceof Number) {
            return ((Number) value).doubleValue() > 0;
        }
        return false;
    }

    default Number getNumber(Object... key) {
        Object value = getObject(key);
        if (value instanceof Number) {
            return (Number) value;
        } else if (value != null) {
            String valueAsString = String.valueOf(value);
            try {
                return Double.parseDouble(valueAsString);
            } catch (Throwable t) {
                try {
                    return Long.parseLong(valueAsString);
                } catch (Throwable t1) {
                }
            }
        }
        return NUMBER_DEFAULT_VALUE;
    }

    default MapSection getMap(Object... key) {
        Object value = getObject(key);
        if (value instanceof MapSection) {
            return (MapSection) value;
        }
        return MAP_DEFAULT_VALUE;
    }

    default ListSection getList(Object... key) {
        Object value = getObject(key);
        if (value instanceof ListSection) {
            return (ListSection) value;
        }
        return LIST_DEFAULT_VALUE;
    }

    default Object getObject(int index, Object... key) {
        Object current = key[index];
        if (current == null) throw new NullPointerException();
        Object scope = getScope(current);
        if (index + 1 < key.length) {
            if (scope instanceof Section) {
                return ((Section) scope).getObject(index + 1, key);
            }
        }
        return scope;
    }

    default Object getObject(Object... key) {
        return key.length == 0 ? null : getObject(0, key);
    }

    default boolean has(Object... key) {
        return getObject(key) != null;
    }

    default int getInt(Object... key) {
        return getNumber(key).intValue();
    }

    default double getDouble(Object... key) {
        return getNumber(key).doubleValue();
    }

    default float getFloat(Object... key) {
        return getNumber(key).floatValue();
    }

    default long getLong(Object... key) {
        return getNumber(key).longValue();
    }

    default byte getByte(Object... key) {
        return getNumber(key).byteValue();
    }

    default short getShort(Object... key) {
        return getNumber(key).shortValue();
    }

    Collection<Object> keys();

    boolean isMap();

    boolean isList();

    Object unwrap();

}
