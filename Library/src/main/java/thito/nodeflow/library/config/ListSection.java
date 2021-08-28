package thito.nodeflow.library.config;

import java.util.*;
import java.util.stream.*;

public class ListSection extends ArrayList<Object> implements Section {
    private Section parent;

    public ListSection() {
        super();
    }

    public ListSection(Collection<?> c) {
        super();
        addAll(c);
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
    public Object set(int index, Object element) {
        element = Section.wrap(element);
        return super.set(index, element);
    }

    @Override
    public boolean add(Object o) {
        o = Section.wrap(o);
        return super.add(o);
    }

    @Override
    public void add(int index, Object element) {
        element = Section.wrap(element);
        super.add(index, element);
    }

    @Override
    public boolean addAll(Collection<?> c) {
        c.forEach(o -> add(c));
        return !c.isEmpty();
    }

    @Override
    public boolean addAll(int index, Collection<?> c) {
        List<Object> wrapped = new ArrayList<>();
        c.forEach(o -> wrapped.add(Section.wrap(o)));
        return super.addAll(index, wrapped);
    }

    @Override
    public String toString() {
        return Section.toString(this);
    }
}
