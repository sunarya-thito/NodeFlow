package thito.nodeflow.internal.ui.dialog.content;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.ui.*;
import thito.nodeflow.api.ui.dialog.Dialog;
import thito.nodeflow.api.ui.dialog.content.*;
import thito.nodeflow.internal.*;

public class MessageContentImpl extends AbstractTitledContent implements MessageContent {
    private I18nItem message;
    private Pos alignment;

    public MessageContentImpl(Dialog.Type type, Dialog.Level level, I18nItem header, Pos alignment, I18nItem message, Pos alignment1) {
        super(type, level, header, alignment);
        this.message = message;
        this.alignment = alignment1;
    }

    @Override
    public I18nItem getMessage() {
        return message;
    }

    @Override
    public void setMessage(I18nItem message) {
        this.message = message;
    }

    @Override
    public Pos getMessageAlignment() {
        return alignment;
    }

    @Override
    public void setMessageAlignment(Pos pos) {
        this.alignment = pos;
    }

    @Override
    public Region impl_createContentPeer() {
        Label label = new Label();
        Toolkit.style(label, "dialog-content-message");
        label.textProperty().bind(getMessage().stringBinding());
        label.setTextAlignment(Toolkit.posToTextAlignment(getMessageAlignment()));
        label.setWrapText(true);
        return new VBox(label);
    }
}
