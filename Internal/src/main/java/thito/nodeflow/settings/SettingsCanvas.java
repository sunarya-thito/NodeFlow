package thito.nodeflow.settings;

import thito.nodeflow.language.I18n;
import thito.nodeflow.project.ProjectProperties;
import thito.nodeflow.settings.canvas.*;

import java.lang.reflect.*;

public abstract class SettingsCanvas {
    public static ReflectedSettingsCategory readCanvas(Class<? extends SettingsCanvas> type) {
        Category category = type.getAnnotation(Category.class);
        if (category == null) throw new IllegalArgumentException("not a proper canvas type, Category annotation is missing!");
        try {
            Constructor<?> constructor = type.getConstructor();
            SettingsCanvas canvas = (SettingsCanvas) constructor.newInstance();
            return new ReflectedSettingsCategory(type.getSimpleName(), I18n.replace(category.value()), category.context(), canvas);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    ProjectProperties project;

    public ProjectProperties getProjectProperties() {
        return project;
    }
}
