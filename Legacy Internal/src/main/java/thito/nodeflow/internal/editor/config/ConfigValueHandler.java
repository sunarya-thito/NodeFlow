package thito.nodeflow.internal.editor.config;

import javafx.beans.property.*;
import javafx.scene.*;
import thito.nodeflow.api.config.*;
import thito.reflectedbytecode.*;

public interface ConfigValueHandler {
    ObjectProperty<Object> valueProperty();
    ConfigValueType getType();
    void save(String key, Section section);
    Node impl_getPeer();
    Reference getCompilableReference();
}
