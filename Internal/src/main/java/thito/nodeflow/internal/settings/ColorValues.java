package thito.nodeflow.internal.settings;

import javafx.scene.paint.*;

import java.util.*;

public class ColorValues {
    private Map<String, Color> colorMap = new HashMap<>();

    public ColorValues(Map<String, Color> colorMap) {
        this.colorMap.putAll(colorMap);
    }

    public Map<String, Color> getColorMap() {
        return colorMap;
    }
}
