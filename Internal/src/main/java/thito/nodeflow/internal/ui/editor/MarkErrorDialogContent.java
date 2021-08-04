package thito.nodeflow.internal.ui.editor;

import javafx.beans.property.*;
import javafx.scene.layout.*;
import thito.nodeflow.api.ui.dialog.*;
import thito.nodeflow.api.ui.dialog.content.*;

public class MarkErrorDialogContent implements DialogContent {

    private StringProperty text = new SimpleStringProperty();
    @Override
    public Pane impl_createPeer(OpenedDialog dialog) {
        return new MarkErrorDialogContentUI(text);
    }

    public StringProperty textProperty() {
        return text;
    }
}
