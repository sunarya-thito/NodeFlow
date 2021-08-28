package thito.nodeflow.library.ui;

import com.sun.javafx.css.*;
import javafx.beans.property.*;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

public class ThemeManager {

    private static ThemeManager instance;

    public static ThemeManager getInstance() {
        return instance;
    }

    private ObjectProperty<Theme> theme = new SimpleObjectProperty<>();
    private Map<Class<?>, StyleSheet> sheetMap = new HashMap<>();

    public static void init() {
        if (instance != null) throw new IllegalStateException("already initialized");
        instance = new ThemeManager();
    }

    public ThemeManager() {
        theme.addListener((obs, old, val) -> {
//            if (old != null) {
////                StyleManager.getInstance().removeUserAgentStylesheet(
////                        "rsrc:Themes/"+URLEncoder.encode(old.getName(), StandardCharsets.UTF_8)+"/StyleSheets/"+Skin.class.getName().replace('.', '/')+".css");
//            }
            updateTheme(val);
//            StyleManager.getInstance().addUserAgentStylesheet(
//                    "rsrc:Themes/"+URLEncoder.encode(val.getName(), StandardCharsets.UTF_8)+"/StyleSheets/"+Skin.class.getName().replace('.', '/')+".css");
        });
    }

    protected void updateTheme(Theme theme) {
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
