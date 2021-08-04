package thito.nodeflow.api.config;

import java.util.Map;

public interface MapSection extends Section, Map<String, Object> {
    MapEditor<MapSection, String, Object> edit();
}
