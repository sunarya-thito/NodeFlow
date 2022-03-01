package thito.nodeflow.ui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import thito.nodeflow.task.TaskThread;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ThemeManager {

    private static ThemeManager instance;

    public static ThemeManager getInstance() {
        return instance;
    }

    private final ObjectProperty<Theme> theme = new SimpleObjectProperty<>();
    private final Map<Class<?>, StyleSheet> sheetMap = new HashMap<>();

    public static void init() {
        if (instance != null) throw new IllegalStateException("already initialized");
        instance = new ThemeManager();
    }

    public ThemeManager() {
        theme.addListener((obs, old, val) -> {
            TaskThread.UI().schedule(() -> {
                updateTheme(val);
            });
        });
    }


    protected void updateTheme(Theme theme) {
        sheetMap.forEach((key, sheet) -> {
            setSheetContents(theme, sheet, key);
        });
    }

    private byte[] read(URL url) {
        try {
            URLConnection urlConnection = url.openConnection();
            urlConnection.setUseCaches(false);
            urlConnection.setDefaultUseCaches(false);
            try (InputStream inputStream = urlConnection.getInputStream()) {
                return inputStream.readAllBytes();
            }
        } catch (FileNotFoundException e) {
            return null;
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

    protected StyleSheet setSheetContents(Theme theme, StyleSheet sheet, Class<?> name) {
        try {
            byte[] localLayout = read(new URL("rsrc:Themes/" + URLEncoder.encode(theme.getName(), StandardCharsets.UTF_8) + "/Layouts/" + name.getName().replace('.', '/') + ".xml"));
            if (localLayout != null) {
                sheet.layoutProperty().set(new String(localLayout, StandardCharsets.UTF_8));
            }
        } catch (MalformedURLException e) {
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
