package thito.nodeflow.internal.config;

import thito.nodeflow.api.config.*;

import java.util.*;

public class ListSectionImpl implements ListSection {
    protected List<Object> list = new ArrayList<>();

    public ListSectionImpl() {
    }

    public ListSectionImpl(Collection<?> c) {
        for (Object element : c) add(element);
    }

    public ListSectionImpl(Object... elements) {
        for (Object element : elements) add(element);
    }

    @Override
    public boolean isMap() {
        return false;
    }

    @Override
    public boolean isList() {
        return true;
    }

    public <T> List<T> filter(Class<T> filter) {
        List<T> filterList = new ArrayList<>(size());
        for (int i = 0; i < list.size(); i++) {
            Object element = list.get(i);
            if (filter == null || filter.isInstance(element)) {
                filterList.add((T) element);
            }
        }
        return filterList;
    }

    @Override
    public Collection<Object> keys() {
        List<Object> keys = new ArrayList<>();
        for (int i = 0; i < size(); i++) keys.add(i);
        return keys;
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    @Override
    public Iterator<Object> iterator() {
        return list.iterator();
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return list.toArray(a);
    }

    @Override
    public boolean add(Object o) {
        return list.add(Section.wrapValue(o));
    }

    @Override
    public boolean remove(Object o) {
        return list.remove(Section.wrapValue(o));
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object object : c) {
            if (!contains(object)) return false;
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<?> c) {
        boolean change = false;
        for (Object o : c) {
            if (add(o)) {
                change = true;
            }
        }
        return change;
    }

    @Override
    public boolean addAll(int index, Collection<?> c) {
        boolean change = false;
        for (Object o : c) {
            if (index <= 0 && add(Section.wrapValue(o))) {
                change = true;
            }
            index--;
        }
        return change;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean change = false;
        for (Object o : c) {
            if (remove(o)) {
                change = true;
            }
        }
        return change;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean change = false;
        for (int i = size() - 1; i >= 0; i--) {
            Object o = get(i);
            if (!c.contains(o)) {
                remove(o);
                change = true;
            }
        }
        return change;
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof List && ((List<?>) o).containsAll(this));
    }

    @Override
    public int hashCode() {
        return list.hashCode();
    }

    @Override
    public Object get(int index) {
        return list.get(index);
    }

    @Override
    public Object set(int index, Object element) {
        return list.set(index, element);
    }

    @Override
    public void add(int index, Object element) {
        list.add(index, Section.wrapValue(element));
    }

    @Override
    public Object remove(int index) {
        return list.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        for (int i = 0; i < size(); i++) {
            Object test = get(i);
            if (Section.equals(test, o)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public Object unwrap() {
        return list;
    }

    @Override
    public int lastIndexOf(Object o) {
        for (int i = size() - 1; i >= 0; i--) {
            Object test = get(i);
            if (Section.equals(test, o)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public ListIterator<Object> listIterator() {
        return list.listIterator();
    }

    @Override
    public ListIterator<Object> listIterator(int index) {
        return list.listIterator(index);
    }

    @Override
    public List<Object> subList(int fromIndex, int toIndex) {
        return new ListSectionImpl(list.subList(fromIndex, toIndex));
    }

    @Override
    public Object getScope(Object key) {
        if (key instanceof Number) {
            return get(((Number) key).intValue());
        }
        if (key instanceof String) {
            try {
                return get(Integer.parseInt((String) key));
            } catch (Throwable t) {
            }
        }
        return null;
    }

    @Override
    public void setScope(Object key, Object value) {
        int targetIndex = -1;
        if (key instanceof Number) {
            targetIndex = ((Number) key).intValue();
        } else if (key instanceof String) {
            try {
                targetIndex = Integer.parseInt((String) key);
            } catch (Throwable t) {
            }
        }
        if (targetIndex >= size()) {
            for (int i = size() - 1; i < targetIndex; i++) {
                add(null);
            }
            add(value);
        } else if (targetIndex >= 0) {
            set(targetIndex, value);
        }
    }

    @Override
    public String toString() {
        return String.valueOf(list);
    }
}
