package thito.nodeflow.internal.editor.config.type;

import javafx.beans.property.*;
import javafx.scene.*;
import javafx.scene.control.*;
import thito.nodeflow.api.config.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.ui.dialog.Dialog;
import thito.nodeflow.api.ui.dialog.button.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.internal.editor.config.*;
import thito.nodeflow.internal.ui.editor.*;
import thito.nodeflow.library.binding.*;
import thito.reflectedbytecode.*;

public class StringValueType implements ConfigValueType {
    @Override
    public String getId() {
        return "string";
    }

    @Override
    public I18nItem getDisplayName() {
        return I18n.$("config-string-type");
    }

    @Override
    public Class<?> getFieldType() {
        return String.class;
    }

    @Override
    public ConfigValueHandler createHandler(Object value) {
        return new ConfigValueHandler() {
            private Label textField = new Label();
            private ObjectProperty<Object> val = new SimpleObjectProperty<>(value == null ? "" : value.toString());

            {
                MappedBidirectionalBinding.bindBidirectional(textField.textProperty(), val, x -> x, x -> x == null ? "" : x.toString());
                textField.getStyleClass().add("text-field-label");
                textField.getStyleClass().add("string-value-type");
                textField.setOnMouseClicked(event -> {
                    TextDialogContent content = new TextDialogContent(textField.textProperty());
                    Dialog dialog = Dialog.createDialog(content, 0, DialogButton.createTextButton(0, 0, I18n.$("button-ok"), null, mouseEvent -> {
                        mouseEvent.close();
                    }));
                    dialog.open(Toolkit.getWindow(textField));
                });
            }

            @Override
            public ObjectProperty<Object> valueProperty() {
                return val;
            }

            @Override
            public ConfigValueType getType() {
                return StringValueType.this;
            }

            @Override
            public void save(String key, Section section) {
                section.set(key, val.get());
            }

            @Override
            public Node impl_getPeer() {
                return textField;
            }

            @Override
            public Reference getCompilableReference() {
                return Reference.javaToReference(textField.getText());
            }

        };
    }
}
