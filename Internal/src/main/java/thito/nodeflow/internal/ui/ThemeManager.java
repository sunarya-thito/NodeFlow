package thito.nodeflow.internal.ui;

import javafx.beans.property.*;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.util.*;

public class ThemeManager {

    private static ThemeManager instance;

    public static ThemeManager getInstance() {
        return instance;
    }

    private final ObjectProperty<Theme> theme = new SimpleObjectProperty<>();
    private final Map<Class<?>, StyleSheet> sheetMap = new HashMap<>();
    private final ColorPalette colorPalette = new ColorPalette();

    public static void init() {
        if (instance != null) throw new IllegalStateException("already initialized");
        instance = new ThemeManager();
    }

    public ThemeManager() {
        theme.addListener((obs, old, val) -> {
            updateTheme(val);
        });
    }

    public ColorPalette getColorPalette() {
        return colorPalette;
    }

    protected void updateTheme(Theme theme) {
        try {
//            URL styleJson = new URL("rsrc:Themes/" + URLEncoder.encode(theme.getName(), StandardCharsets.UTF_8) + "/Colors.json");
            try (InputStream inputStream = new FileInputStream("Themes/" + theme.getName() + "/Colors.json")) {
                colorPalette.load(inputStream);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        sheetMap.forEach((key, sheet) -> {
            setSheetContents(theme, sheet, key);
        });
    }

    protected StyleSheet setSheetContents(Theme theme, StyleSheet sheet, Class<?> name) {
        try{
            URLConnection connection = new URL("rsrc:Themes/"+URLEncoder.encode(theme.getName(), StandardCharsets.UTF_8)+"/Layouts/"+name.getName().replace('.', '/')+".xml").openConnection();
            connection.setUseCaches(false);
            connection.setDefaultUseCaches(false);
            try (InputStream inputStream = connection.getInputStream()) {
                sheet.layoutProperty().set(new String(inputStream.readAllBytes(), StandardCharsets.UTF_8));
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        Class<?> clazz = name;
        sheet.getCssFiles().clear();
        while (Skin.class.isAssignableFrom(clazz)) {
            sheet.getCssFiles().add(0, "rsrc:Themes/"+URLEncoder.encode(theme.getName(), StandardCharsets.UTF_8)+"/StyleSheets/"+clazz.getName().replace('.', '/')+".css");
            clazz = clazz.getSuperclass();
        }
        return sheet;
    }

    public ObjectProperty<Theme> themeProperty() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme.set(theme);
    }

    public Theme getTheme() {
        return theme.get();
    }

    public StyleSheet getStyleSheet(Skin skin) {
        return sheetMap.computeIfAbsent(skin.getClass(), name -> new StyleSheet());
    }
}
