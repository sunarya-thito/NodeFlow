package thito.nodeflow.library.config;

import org.yaml.snakeyaml.*;

import java.io.*;
import java.util.*;

public interface Section {
    static String toString(Section section) {
        DumperOptions options = new DumperOptions();
        options.setIndent(4);
        options.setAllowUnicode(true);
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(options);
        return yaml.dumpAsMap(section);
    }
    static MapSection parseToMap(Reader reader) {
        Yaml yaml = new Yaml();
        return new MapSection(yaml.loadAs(reader, Map.class));
    }
    Set<String> getKeys();
    default Set<String> getPaths() {
        Set<String> paths = new HashSet<>();
        for (String k : getKeys()) {
            Object lookup = getInScope(k).orElse(null);
            if (lookup instanceof Section) {
                for (String p : ((Section) lookup).getPaths()) {
                    paths.add(k + "." + p);
                }
            }
        }
        return paths;
    }
    Section getParent();
    Optional<?> getInScope(String key);
    void setInScope(String key, Object value);
    default void set(String path, Object value) {
        String[] paths = path.split("\\.");
        Object lookup = this;
        for (int i = 0; i < paths.length - 1; i++) {
            Section oldLookup = (Section) lookup;
            lookup = oldLookup.getInScope(paths[i]).orElse(null);
            if (!(lookup instanceof Section)) {
                oldLookup.setInScope(paths[i], lookup = new MapSection());
            }
        }
        if (paths.length > 0) {
            ((Section) lookup).setInScope(paths[paths.length - 1], value);
        }
    }
    default Optional<?> getObject(String path) {
        String[] paths = path.split("\\.");
        Object lookup = this;
        for (String s : paths) {
            if (lookup instanceof Section) {
                lookup = ((Section) lookup).getInScope(s).orElse(null);
            } else {
                return Optional.empty();
            }
        }
        return Optional.ofNullable(lookup);
    }
    default <T extends Enum<T>> Optional<T> getEnum(String path, Class<T> clz) {
        return getObject(path).map(o -> {
            try {
                return Enum.valueOf(clz, String.valueOf(o));
            } catch (Throwable t) {
                return null;
            }
        });
    }
    default Optional<String> getString(String path) {
        return getObject(path).map(String::valueOf);
    }
    default Optional<Integer> getInteger(String path) {
        return getObject(path).map(o -> {
            try {
                return Integer.valueOf(String.valueOf(o));
            } catch (Throwable t) {
                return null;
            }
        });
    }
    default Optional<Double> getDouble(String path) {
        return getObject(path).map(o -> {
            try {
                return Double.valueOf(String.valueOf(o));
            } catch (Throwable t) {
                return null;
            }
        });
    }
    default Optional<Long> getLong(String path) {
        return getObject(path).map(o -> {
            try {
                return Long.valueOf(String.valueOf(o));
            } catch (Throwable t) {
                return null;
            }
        });
    }
    default Optional<Float> getFloat(String path) {
        return getObject(path).map(o -> {
            try {
                return Float.valueOf(String.valueOf(o));
            } catch (Throwable t) {
                return null;
            }
        });
    }
    default Optional<Short> getShort(String path) {
        return getObject(path).map(o -> {
            try {
                return Short.valueOf(String.valueOf(o));
            } catch (Throwable t) {
                return null;
            }
        });
    }
    default Optional<Byte> getByte(String path) {
        return getObject(path).map(o -> {
            try {
                return Byte.valueOf(String.valueOf(o));
            } catch (Throwable t) {
                return null;
            }
        });
    }
    default Optional<Character> getCharacter(String path) {
        return getObject(path).map(o -> {
            String text = String.valueOf(o);
            return text.isEmpty() ? null : text.charAt(0);
        });
    }
    default Optional<Boolean> getBoolean(String path) {
        return getObject(path).map(o -> {
            String text = String.valueOf(o);
            return text.equals("true") ? true : text.equals("false") ? false : null;
        });
    }
    default Optional<MapSection> getMap(String path) {
        return getObject(path).map(o -> {
            if (o instanceof Map) {
                MapSection mapSection = new MapSection((Map<String, ?>) o);
                mapSection.setParent(this);
                return mapSection;
            }
            return null;
        });
    }
    default Optional<ListSection> getList(String path) {
        return getObject(path).map(o -> {
            if (o instanceof List) {
                ListSection list = new ListSection((List<?>) o);
                list.setParent(this);
                return list;
            }
            ListSection list = new ListSection(Collections.singleton(o));
            list.setParent(this);
            return list;
        });
    }
}
