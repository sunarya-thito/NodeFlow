package thito.nodeflow.internal.editor.config;

import javafx.beans.property.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.ui.dialog.Dialog;
import thito.nodeflow.internal.*;
import thito.nodeflow.internal.ui.dialog.*;
import thito.nodeflow.library.ui.layout.*;

public class ConfigFileItemUI extends UIComponent {
    private ConfigFileModule.Value value;
    private ConfigValueHandler handler;

    @Component("name")
    private ObjectProperty<TextField> name = new SimpleObjectProperty<>();

    @Component("viewport")
    private ObjectProperty<BorderPane> viewport = new SimpleObjectProperty<>();

    @Component("delete")
    private ObjectProperty<BorderPane> delete = new SimpleObjectProperty<>();

    private ConfigFileUI parent;

    public ConfigFileItemUI(ConfigFileUI parent, ConfigFileModule.Value value) {
        this.parent = parent;
        this.value = value;
        handler = ConfigFileManager.getManager().getType(value.getType()).createHandler(value.getConstantValue());
        setLayout(Layout.loadLayout("ConfigFileItemUI"));
    }

    @Override
    protected void onLayoutReady() {
        name.get().setText(value.getName());
        name.get().setEditable(false);
        name.get().setOnMouseClicked(event -> {
            if (event.getClickCount() >= 2) {
                name.get().setEditable(true);
                name.get().selectAll();
            }
        });
        name.get().setOnAction(event -> {
            name.get().setEditable(false);
        });
        name.get().focusedProperty().addListener((obs, old, val) -> {
            if (!val) {
                name.get().setEditable(false);
            }
        });
        name.get().textProperty().addListener((obs, old, val) -> {
            for (ConfigFileModule.Value value : parent.getModule().getValues()) {
                if (val.equals(value.getName())) {
                    name.get().setText("Duplicate "+val);
                    return;
                }
            }
            value.setName(val);
            if (val.isEmpty()) name.get().setText(old);
        });
        viewport.get().setCenter(handler.impl_getPeer());
        handler.valueProperty().addListener((obs, old, val) -> {
            value.setConstantValue(val);
        });
        delete.get().setOnMouseClicked(event -> {
            Dialogs.ask(Toolkit.getWindow(this), I18n.$("delete-variable-title"),
                    I18n.$("delete-variable-message").format(value.getName()),
                    Dialog.Type.QUESTION,
                    Dialog.Level.WARN,
                    result -> {
                        if (result) {
                            parent.getModule().getValues().remove(value);
                        }
                    });
        });
    }
}
