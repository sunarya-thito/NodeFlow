package thito.nodeflow.internal.settings;

import javafx.beans.property.*;
import javafx.beans.value.*;
import thito.nodeflow.library.language.*;

import java.lang.annotation.*;
import java.util.*;

public class SettingsProperty<T> extends SimpleObjectProperty<T> {
    Class<T> type;
    String fieldName;
    Map<Class<? extends Annotation>, Annotation> annotationMap = new HashMap<>();
    SettingsCategory category;
    private T defaultValue;
    private I18n name;

    public SettingsProperty(I18n name, T initialValue) {
        super(initialValue);
        this.name = name;
    }

    public SettingsProperty(Class<T> type, String fieldName, I18n displayName, T initialValue) {
        super(initialValue);
        this.defaultValue = initialValue;
        this.type = type;
        this.fieldName = fieldName;
        this.name = displayName;
    }

    public SettingsCategory getCategory() {
        return category;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public <T extends Annotation> T getAnnotated(Class<T> annotated) {
        return (T) annotationMap.get(annotated);
    }

    @Override
    public String getName() {
        return fieldName;
    }

    public I18n displayNameProperty() {
        return name;
    }

    public Class<T> getType() {
        return type;
    }

    public <K> K value() {
        return (K) get();
    }

    public void addListenerAndFire(ChangeListener<? super T> listener) {
        listener.changed(this, null, get());
        addListener(listener);
    }
}
