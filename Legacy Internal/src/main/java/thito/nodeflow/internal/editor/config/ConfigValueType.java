package thito.nodeflow.internal.editor.config;

import thito.nodeflow.api.locale.*;

public interface ConfigValueType {
    String getId();
    I18nItem getDisplayName();
    ConfigValueHandler createHandler(Object value);
    Class<?> getFieldType();
}
