package thito.nodeflow.internal.ui.dialog.button;

import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.ui.*;
import thito.nodeflow.api.ui.action.*;
import thito.nodeflow.api.ui.dialog.button.*;

import java.util.function.*;

public class TextDialogButtonImpl extends AbstractDialogButton implements TextDialogButton {
    private I18nItem label;
    private Icon icon;

    public TextDialogButtonImpl(int id, int behaviour, Consumer<ClickAction> action, I18nItem label, Icon icon) {
        super(id, behaviour, action);
        this.label = label;
        this.icon = icon;
    }

    @Override
    public I18nItem getLabel() {
        return label;
    }

    @Override
    public void setLabel(I18nItem label) {
        this.label = label;
    }

    @Override
    public Icon getIcon() {
        return icon;
    }

    @Override
    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    @Override
    public TextButtonPeer createPeer() {
        return new TextButtonPeerImpl(this);
    }

}
