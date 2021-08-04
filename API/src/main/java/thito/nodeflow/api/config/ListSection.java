package thito.nodeflow.api.config;

import java.util.List;

public interface ListSection extends Section, List<Object> {
    <T> List<T> filter(Class<T> classFiltered);
}
