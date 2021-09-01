package thito.nodeflow.library.ui;

import com.google.gson.*;
import com.google.gson.reflect.*;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.scene.paint.*;

import java.io.*;
import java.nio.charset.*;
import java.util.*;

public class ColorPalette {
    private static String toHex(Color color) {
        int red = (int) (color.getRed() * 255);
        int green = (int) (color.getGreen() * 255);
        int blue = (int) (color.getBlue() * 255);
        int alpha = (int) (color.getOpacity() * 255);
        return "#" + hex(red) + hex(green) + hex(blue) + hex(alpha);
    }

    public static String hex(int num) {
        StringBuilder x = new StringBuilder(Integer.toHexString(num));
        while (x.length() < 2) x.insert(0, "0");
        return x.toString();
    }

    private ObservableMap<String, Color> colorMap = FXCollections.observableMap(new LinkedHashMap<>()); // to keep it sorted
    private ObjectProperty<Map<String, Color>> overrideMap = new SimpleObjectProperty<>();
    private StringProperty style = new SimpleStringProperty();

    public ColorPalette() {
        style.bind(Bindings.createStringBinding(() -> {
            StringBuilder builder = new StringBuilder();
            colorMap.forEach((var, color) -> {
                Map<String, Color> overrideMap = this.overrideMap.get();
                if (overrideMap != null) {
                    Color replacement = overrideMap.get(var);
                    if (replacement != null) {
                        color = replacement;
                    }
                }
                builder.append(var).append(": ").append(toHex(color)).append("; ");
            });
            return builder.toString();
        }, colorMap, overrideMap));
    }

    public ObjectProperty<Map<String, Color>> overrideMapProperty() {
        return overrideMap;
    }

    public StringProperty styleProperty() {
        return style;
    }

    public void load(InputStream inputStream) throws IOException {
        colorMap.clear();
        Gson gson = new Gson();
        List<NamedColorCanvas> list = gson.fromJson(new InputStreamReader(inputStream, StandardCharsets.UTF_8), new TypeToken<List<NamedColorCanvas>>() {}.getType());
        for (NamedColorCanvas i : list) {
            colorMap.put(i.getName(), i.getValue().toColor());
        }
    }

    public void save(OutputStream outputStream) throws IOException {
        List<NamedColorCanvas> list = new ArrayList<>();
        colorMap.forEach((key, value) -> {
            list.add(new NamedColorCanvas(key, new ColorCanvas(value)));
        });
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        outputStream.write(gson.toJson(list).getBytes(StandardCharsets.UTF_8));
    }

    public Map<String, Color> getColorMap() {
        return colorMap;
    }

    public static class NamedColorCanvas {
        private String name;
        private ColorCanvas value;

        public NamedColorCanvas(String name, ColorCanvas value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public ColorCanvas getValue() {
            return value;
        }
    }

    public static class ColorCanvas {
        private int red, green, blue, alpha;

        public ColorCanvas(Color c) {
            red = (int) (c.getRed() * 255);
            green = (int) (c.getGreen() * 255);
            blue = (int) (c.getBlue() * 255);
            alpha = (int) (c.getOpacity() * 255);
        }

        public Color toColor() {
            return Color.rgb(red, green, blue, alpha / 255d);
        }
    }
}
