package thito.nodeflow.internal.ui.editor;

import javafx.beans.property.*;
import javafx.scene.layout.*;
import thito.nodeflow.api.ui.dialog.*;
import thito.nodeflow.api.ui.dialog.content.*;

public class TextDialogContent implements DialogContent {
    private StringProperty stringProperty;

    public TextDialogContent(StringProperty stringProperty) {
        this.stringProperty = stringProperty;
    }

    @Override
    public Pane impl_createPeer(OpenedDialog dialog) {
        return new TextDialogContentUI(stringProperty);
    }

}
