package thito.nodeflow.internal.editor.record;

import javafx.beans.property.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.ui.dialog.Dialog;
import thito.nodeflow.internal.*;
import thito.nodeflow.internal.ui.dialog.*;
import thito.nodeflow.library.ui.layout.*;

public class RecordFileItemUI extends UIComponent {
    @Component("name")
    private ObjectProperty<TextField> name = new SimpleObjectProperty<>();

    @Component("viewport")
    private ObjectProperty<BorderPane> viewport = new SimpleObjectProperty<>();

    @Component("delete")
    private ObjectProperty<BorderPane> delete = new SimpleObjectProperty<>();

    private RecordFileUI parent;
    private RecordItem item;

    public RecordFileItemUI(RecordFileUI parent, RecordItem item) {
        this.parent = parent;
        this.item = item;
        setLayout(Layout.loadLayout("RecordFileItemUI"));
    }

    @Override
    protected void onLayoutReady() {
        name.get().setText(item.getName());
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
            for (RecordItem value : parent.getSession().getModule().getItems()) {
                if (val.equals(value.getName())) {
                    name.get().setText("Duplicate "+val);
                    return;
                }
            }
            item.setName(val);
            if (val.isEmpty()) name.get().setText(old);
        });
        viewport.get().setCenter(new Label(item.getType().getName()));
        delete.get().setOnMouseClicked(event -> {
            Dialogs.ask(Toolkit.getWindow(this), I18n.$("delete-variable-title"),
                    I18n.$("delete-variable-message").format(item.getName()),
                    thito.nodeflow.api.ui.dialog.Dialog.Type.QUESTION,
                    Dialog.Level.WARN,
                    result -> {
                        if (result) {
                            parent.getSession().getModule().getItems().remove(item);
                        }
                    });
        });
    }
}
