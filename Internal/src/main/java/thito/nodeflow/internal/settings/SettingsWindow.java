package thito.nodeflow.internal.settings;

import javafx.collections.*;
import thito.nodeflow.internal.ui.*;

import java.lang.reflect.*;
import java.util.logging.*;

public class SettingsWindow {
    private static Logger logger = Logger.getLogger("Settings");
    private StandardWindow window = new StandardWindow();
    private ObservableList<SettingsCategory> categoryList = FXCollections.observableArrayList();

    public SettingsWindow(Class<? extends Settings>... rootCategories) {
        for (Class<? extends Settings> category : rootCategories) {
            try {
                SettingsCategory c = scan(category);
                categoryList.add(c);
            } catch (Exception e) {
                logger.log(Level.WARNING, "Failed to scan category "+category.getName(), e);
            }
        }
    }

    private SettingsCategory scan(Class<? extends Settings> settingsClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Settings settings = settingsClass.getConstructor().newInstance();
        SettingsCategory category = new SettingsCategory(settings.description());
        Class<?> parent = settingsClass;
        while (Settings.class.isAssignableFrom(parent)) {
            for (Field field : parent.getDeclaredFields()) {
                field.setAccessible(true);
                if (SettingsProperty.class.isAssignableFrom(field.getType())) {
                    category.settingsPropertyList.add((SettingsProperty<?>) field.get(settings));
                }
            }
            parent = parent.getSuperclass();
        }
        for (Class<?> clazz : settings.getClass().getClasses()) {
            if (Settings.class.isAssignableFrom(clazz)) {
                try {
                    SettingsCategory subCategory = scan(clazz.asSubclass(Settings.class));
                    category.subCategory.add(subCategory);
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Failed to scan category "+clazz.getName(), e);
                }
            }
        }
        return category;
    }
}
