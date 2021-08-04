package thito.nodeflow.api.ui.dialog.content;

import javafx.scene.layout.Pane;
import thito.nodeflow.api.NodeFlow;
import thito.nodeflow.api.locale.I18nItem;
import thito.nodeflow.api.ui.Pos;
import thito.nodeflow.api.ui.dialog.Dialog;
import thito.nodeflow.api.ui.dialog.OpenedDialog;

public interface DialogContent {
    static MessageContent createMessageContent(Dialog.Type type, Dialog.Level level, I18nItem header, Pos headerAlignment, I18nItem message, Pos messageAlignment) {
        return NodeFlow.getApplication().getUIManager().getDialogManager().createMessageContent(type, level, header, headerAlignment, message, messageAlignment);
    }

    static ActionContent createActionContent(I18nItem header, Pos headerAlignment, ActionContent.Action... actions) {
        return NodeFlow.getApplication().getUIManager().getDialogManager().createActionContent(header, headerAlignment, actions);
    }

    Pane impl_createPeer(OpenedDialog dialog);
}
