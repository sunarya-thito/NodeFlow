package thito.nodeflow.config;

import java.util.Arrays;

public class Path {
    private final String[] keys;

    public Path(Path parent, String...keys) {
        String[] parentKeys = parent.keys;
        this.keys = new String[parentKeys.length + keys.length];
        System.arraycopy(parentKeys, 0, this.keys, 0, parentKeys.length);
        System.arraycopy(keys, 0, this.keys, parentKeys.length, keys.length);
    }

    public Path(String... keys) {
        this.keys = keys;
    }

    public String[] getKeys() {
        return keys;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Path that = (Path) o;
        return Arrays.equals(keys, that.keys);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(keys);
    }

    @Override
    public String toString() {
        return String.join(" > ", keys);
    }
}
