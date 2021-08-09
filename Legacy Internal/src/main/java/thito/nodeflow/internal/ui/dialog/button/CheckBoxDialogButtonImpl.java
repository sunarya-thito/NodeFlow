package thito.nodeflow.internal.ui.dialog.button;

import javafx.beans.property.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.ui.action.*;
import thito.nodeflow.api.ui.dialog.button.*;

import java.util.function.*;

public class CheckBoxDialogButtonImpl extends AbstractDialogButton implements CheckBoxDialogButton {

    private I18nItem label;
    private BooleanProperty checked = new SimpleBooleanProperty();

    public CheckBoxDialogButtonImpl(int id, int behaviour, Consumer<ClickAction> action, I18nItem label) {
        super(id, behaviour, action);
        this.label = label;
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
    public CheckBoxButtonPeer createPeer() {
        return new CheckBoxButtonPeerImpl(this);
    }

    @Override
    public BooleanProperty impl_checkedProperty() {
        return checked;
    }
}
