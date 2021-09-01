package thito.nodeflow.internal.settings.parser;

import javafx.scene.paint.*;
import thito.nodeflow.internal.settings.*;
import thito.nodeflow.library.config.*;

import java.util.*;

public class ColorValuesParser implements SettingsParser<ColorValues> {
    @Override
    public Optional<ColorValues> fromConfig(Section source, String key) {
        return source.getMap(key).map(section -> {
            Map<String, Color> colorMap = new HashMap<>();
            for (String k : section.getKeys()) {
                colorMap.put(k, Color.web(section.getString(k).orElseThrow()));
            }
            return new ColorValues(colorMap);
        });
    }

    @Override
    public void toConfig(Section source, String key, ColorValues value) {
        value.getColorMap().forEach((k, v) -> {
            int red = (int) (v.getRed() * 255);
            int green = (int) (v.getGreen() * 255);
            int blue = (int) (v.getBlue() * 255);
            int alpha = (int) (v.getOpacity() * 255);
            source.set(key + "." + k, "#" + hex(red) + hex(green) + hex(blue) + hex(alpha));
        });
    }

    public static String hex(int num) {
        StringBuilder x = new StringBuilder(Integer.toHexString(num));
        while (x.length() < 2) x.insert(0, "0");
        return x.toString();
    }
}
