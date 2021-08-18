package thito.nodeflow.library.ui;

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

    public ThemeManager() {
        instance = this;
        theme.addListener((obs, old, val) -> {
            updateTheme(val);
        });
    }

    protected void updateTheme(Theme theme) {
        sheetMap.forEach((key, sheet) -> {
            setSheetContents(theme, sheet, key);
        });
    }

    protected StyleSheet setSheetContents(Theme theme, StyleSheet sheet, Class<?> name) {
        try{
            URLConnection connection = new URL("rsrc:Themes/"+URLEncoder.encode(theme.getName(), StandardCharsets.UTF_8)+"/"+name.getSimpleName()+".xml").openConnection();
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
            sheet.getCssFiles().add(0, "rsrc:Themes/"+URLEncoder.encode(theme.getName(), StandardCharsets.UTF_8)+"/"+clazz.getSimpleName()+".css");
            clazz = clazz.getSuperclass();
        }
        return sheet;
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
