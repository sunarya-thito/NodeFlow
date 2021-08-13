package thito.nodeflow.library.ui;

import javafx.beans.property.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

public class ThemeManager {

    private static ThemeManager instance;

    public static ThemeManager getInstance() {
        return instance;
    }

    private ObjectProperty<Theme> theme = new SimpleObjectProperty<>();
    private Map<String, Icon> iconMap = new HashMap<>();
    private Map<Class<?>, StyleSheet> sheetMap = new HashMap<>();

    public ThemeManager() {
        instance = this;
        theme.addListener((obs, old, val) -> {
            updateTheme(val);
        });
    }

    protected void updateTheme(Theme theme) {
        iconMap.forEach((key, icon) -> {
            icon.imageProperty().set(theme.loadImage("Icons/"+key+".png"));
        });
        sheetMap.forEach((key, sheet) -> {
            setSheetContents(theme, sheet, key);
        });
    }

    protected StyleSheet setSheetContents(Theme theme, StyleSheet sheet, Class<?> name) {
        try {
            sheet.layoutProperty().set(Files.readString(new File(theme.getRoot(), name.getSimpleName()+".xml").toPath(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Class<?> clazz = name;
        sheet.getCssFiles().clear();
        while (Skin.class.isAssignableFrom(clazz)) {
            sheet.getCssFiles().add(new File(theme.getRoot(), clazz.getSimpleName()+".css").getAbsolutePath());
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

    public Icon getIcon(String icon) {
        return iconMap.computeIfAbsent(icon, name -> new Icon(getTheme().loadImage("Icons/"+name+".png")));
    }

    public StyleSheet getStyleSheet(Skin skin) {
        return sheetMap.computeIfAbsent(skin.getClass(), name ->
                setSheetContents(getTheme(), new StyleSheet(), skin.getClass()));
    }
}
