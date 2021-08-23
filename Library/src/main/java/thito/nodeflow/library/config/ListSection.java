package thito.nodeflow.library.config;

import java.util.*;
import java.util.stream.*;

public class ListSection extends ArrayList<Object> implements Section {
    private Section parent;

    public ListSection() {
        super();
    }

    public ListSection(Collection<?> c) {
        super(c);
    }

    protected void setParent(Section parent) {
        this.parent = parent;
    }

    @Override
    public Section getParent() {
        return parent;
    }

    @Override
    public Set<String> getKeys() {
        return IntStream.range(0, size()).mapToObj(String::valueOf).collect(Collectors.toSet());
    }

    @Override
    public Optional<?> getInScope(String key) {
        try {
            return Optional.ofNullable(get(Integer.parseInt(key)));
        } catch (Throwable t) {
        }
        return Optional.empty();
    }

    @Override
    public void setInScope(String key, Object value) {
        try {
            set(Integer.parseInt(key), value);
        } catch (Throwable t) {
        }
    }

    @Override
    public String toString() {
        return Section.toString(this);
    }
}
