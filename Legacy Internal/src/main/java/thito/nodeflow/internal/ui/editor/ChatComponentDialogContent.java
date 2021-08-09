package thito.nodeflow.internal.ui.editor;

import javafx.beans.property.*;
import javafx.scene.layout.*;
import net.md_5.bungee.api.chat.*;
import thito.nodeflow.api.ui.dialog.*;
import thito.nodeflow.api.ui.dialog.content.*;

public class ChatComponentDialogContent implements DialogContent {
    private ObjectProperty<BaseComponent[]> stringProperty;
    private boolean hoverable = true, clickable = true;

    public ChatComponentDialogContent(ObjectProperty<BaseComponent[]> stringProperty) {
        this.stringProperty = stringProperty;
    }

    public void setHoverable(boolean hoverable) {
        this.hoverable = hoverable;
    }

    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

    @Override
    public Pane impl_createPeer(OpenedDialog dialog) {
        return new ChatComponentDialogContentUI(stringProperty, hoverable, clickable);
    }

}
