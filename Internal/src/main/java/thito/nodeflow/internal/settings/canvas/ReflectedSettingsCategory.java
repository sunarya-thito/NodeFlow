package thito.nodeflow.internal.settings.canvas;

import javafx.beans.property.*;
import thito.nodeflow.internal.language.*;
import thito.nodeflow.internal.settings.*;

import java.lang.reflect.*;

public class ReflectedSettingsCategory extends SettingsCategory {
    private SettingsCanvas canvas;

    public ReflectedSettingsCategory(String key, I18n displayName, SettingsContext context, SettingsCanvas canvas) {
        super(key, displayName, context);
        this.canvas = canvas;

        Class<?> scan = getClass();
        while (scan != null) {
            for (Field field : scan.getDeclaredFields()) {
                field.setAccessible(true);
                Item annotation = field.getAnnotation(Item.class);
                if (annotation == null || !Property.class.isAssignableFrom(field.getType())) continue;
                ReflectedSettingsItem<?> item = new ReflectedSettingsItem<>(field.getName(), I18n.replace(annotation.value()), canvas, field);
                getItems().add(item);
            }
            for (Class<?> inner : scan.getDeclaredClasses()) {
                if (SettingsCanvas.class.isAssignableFrom(inner)) {
                    getSubCategories().add(SettingsCanvas.readCanvas((Class<? extends SettingsCanvas>) inner));
                }
            }
            scan = scan.getSuperclass();
        }

    }

    public SettingsCanvas getCanvas() {
        return canvas;
    }
}
