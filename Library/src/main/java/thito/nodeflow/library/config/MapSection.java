package thito.nodeflow.library.config;

import java.util.*;

public class MapSection extends HashMap<String, Object> implements Section {
    private Section parent;

    public MapSection() {
        super();
    }

    public MapSection(Map<? extends String, ?> m) {
        super(m);
    }

    protected void setParent(Section parent) {
        this.parent = parent;
    }

    @Override
    public Section getParent() {
        return parent;
    }

    @Override
    public void setInScope(String key, Object value) {
        put(key, value);
    }

    @Override
    public Set<String> getKeys() {
        return keySet();
    }

    @Override
    public Optional<?> getInScope(String key) {
        return Optional.ofNullable(get(key));
    }

    @Override
    public String toString() {
        return Section.toString(this);
    }
}
