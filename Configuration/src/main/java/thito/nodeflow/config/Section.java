package thito.nodeflow.config;

import org.yaml.snakeyaml.*;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public interface Section {
    static Object wrap(Object o) {
        if (o instanceof Section) return o;
        if (o instanceof List) {
            return new ListSection((List<?>) o);
        }
        if (o instanceof Map) {
            return new MapSection((Map<?, ?>) o);
        }
        return o;
    }
    static String getName(Path path) {
        String[] keys = path.getKeys();
        return keys[keys.length - 1];
    }
    static Section wrapParent(Section parent, String name, Section current) {
        if (current.getParent() != null && current.getParent() != parent) {
            if (current instanceof MapSection) {
                MapSection mapSection = new MapSection((MapSection) current);
                mapSection.setParent(parent, name);
                return mapSection;
            }
            if (current instanceof ListSection) {
                ListSection objects = new ListSection((ListSection) current);
                objects.setParent(parent, name);
                return objects;
            }
        } else {
            if (current instanceof MapSection) {
                ((MapSection) current).setParent(parent, name);
            }
            if (current instanceof ListSection) {
                ((ListSection) current).setParent(parent, name);
            }
        }
        return current;
    }
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

    default Set<Path> getPaths() {
        Set<Path> paths = new HashSet<>();
        for (String k : getKeys()) {
            Object lookup = getInScope(k).orElse(null);
            if (lookup instanceof Section) {
                Path parent = new Path(k);
                for (Path p : ((Section) lookup).getPaths()) {
                    paths.add(new Path(parent, p.getKeys()));
                }
            }
        }
        return paths;
    }

    default Path getPath() {
        ArrayList<String> parentPath = new ArrayList<>();
        parentPath.add(getName());
        Section parent;
        while ((parent = getParent()) != null) {
            parentPath.add(0, parent.getName());
        }
        return new Path(parentPath.toArray(new String[0]));
    }

    default void set(Path path, Object value) {
        String[] paths = path.getKeys();
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
    default Optional<?> getObject(Path path) {
        String[] paths = path.getKeys();
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
    default <T extends Enum<T>> Optional<T> getEnum(Path path, Class<T> clz) {
        return getObject(path).map(o -> {
            try {
                return Enum.valueOf(clz, String.valueOf(o));
            } catch (Throwable t) {
                return null;
            }
        });
    }
    default Optional<String> getString(Path path) {
        return getObject(path).map(String::valueOf);
    }
    default Optional<Integer> getInteger(Path path) {
        return getObject(path).map(o -> {
            try {
                return Integer.valueOf(String.valueOf(o));
            } catch (Throwable t) {
                return null;
            }
        });
    }
    default Optional<Double> getDouble(Path path) {
        return getObject(path).map(o -> {
            try {
                return Double.valueOf(String.valueOf(o));
            } catch (Throwable t) {
                return null;
            }
        });
    }
    default Optional<Long> getLong(Path path) {
        return getObject(path).map(o -> {
            try {
                return Long.valueOf(String.valueOf(o));
            } catch (Throwable t) {
                return null;
            }
        });
    }
    default Optional<Float> getFloat(Path path) {
        return getObject(path).map(o -> {
            try {
                return Float.valueOf(String.valueOf(o));
            } catch (Throwable t) {
                return null;
            }
        });
    }
    default Optional<Short> getShort(Path path) {
        return getObject(path).map(o -> {
            try {
                return Short.valueOf(String.valueOf(o));
            } catch (Throwable t) {
                return null;
            }
        });
    }
    default Optional<Byte> getByte(Path path) {
        return getObject(path).map(o -> {
            try {
                return Byte.valueOf(String.valueOf(o));
            } catch (Throwable t) {
                return null;
            }
        });
    }
    default Optional<Character> getCharacter(Path path) {
        return getObject(path).map(o -> {
            String text = String.valueOf(o);
            return text.isEmpty() ? null : text.charAt(0);
        });
    }
    default Optional<Boolean> getBoolean(Path path) {
        return getObject(path).map(o -> {
            String text = String.valueOf(o);
            return text.equals("true") ? Boolean.TRUE : text.equals("false") ? Boolean.FALSE : null;
        });
    }
    default Optional<MapSection> getMap(Path path) {
        return getObject(path).map(o -> {
            if (o instanceof Map) {
                if (o instanceof MapSection) return (MapSection) o;
                MapSection mapSection = new MapSection((Map<?, ?>) o);
                mapSection.setParent(this, Section.getName(path));
                return mapSection;
            }
            return null;
        });
    }
    default Optional<ListSection> getList(Path path) {
        return getObject(path).map(o -> {
            if (o instanceof List) {
                if (o instanceof ListSection) {
                    return (ListSection) o;
                }
                ListSection list = new ListSection((List<?>) o);
                list.setParent(this, Section.getName(path));
                return list;
            }
            ListSection list = new ListSection(Collections.singleton(o));
            list.setParent(this, Section.getName(path));
            return list;
        });
    }
    default Optional<ListSection> getOrCreateList(Path path) {
        ListSection listSection = getList(path).orElse(null);
        if (listSection == null) {
            set(path, listSection = new ListSection());
        }
        return Optional.of(listSection);
    }
    default Optional<MapSection> getOrCreateMap(Path path) {
        MapSection mapSection = getMap(path).orElse(null);
        if (mapSection == null) {
            set(path, mapSection = new MapSection());
        }
        return Optional.of(mapSection);
    }
    String getName();
    default Section getRoot() {
        Section current = this;
        while (current.getParent() != null) {
            current = current.getParent();
        }
        return current;
    }
    Section getParent();
    Optional<?> getInScope(String key);
    void setInScope(String key, Object value);
    default Optional<MapSection> asMap() {
        return this instanceof MapSection ? Optional.of((MapSection) this) : Optional.empty();
    }
    default Optional<ListSection> asList() {
        return this instanceof ListSection ? Optional.of((ListSection) this) : Optional.empty();
    }
}
