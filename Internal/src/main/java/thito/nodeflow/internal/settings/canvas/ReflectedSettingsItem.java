package thito.nodeflow.internal.settings.canvas;

import javafx.beans.property.*;
import thito.nodeflow.internal.language.*;
import thito.nodeflow.internal.settings.*;

import java.lang.reflect.*;

public class ReflectedSettingsItem<T> extends SettingsItem<T> {
    private static Object getOrNull(Field field, SettingsCanvas canvas) {
        try {
            return field.get(canvas);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }
    private static Class<?> parameterType(Type type) {
        return (Class<?>) ((ParameterizedType) type).getActualTypeArguments()[0];
    }
    private SettingsCanvas canvas;
    private Field field;
    private Property<T> value;

    public ReflectedSettingsItem(String key, I18n displayName, SettingsCanvas canvas, Field field) {
        super(key, displayName, (Class<T>) parameterType(field.getGenericType()), ((Property<T>) getOrNull(field, canvas)).getValue());
        this.canvas = canvas;
        this.field = field;
        this.value = new SimpleObjectProperty<>((T) getOrNull(field, canvas)) {
            @Override
            protected void invalidated() {
                super.invalidated();
                try {
                    field.set(canvas, get());
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        };
    }

    @Override
    public Property<T> valueProperty() {
        return value;
    }

    public Field getField() {
        return field;
    }

    public SettingsCanvas getCanvas() {
        return canvas;
    }
}
