package thito.nodeflow.api.ui.dialog.button;

import javafx.beans.property.BooleanProperty;
import thito.nodeflow.api.NodeFlow;
import thito.nodeflow.api.locale.I18nItem;
import thito.nodeflow.api.ui.Icon;
import thito.nodeflow.api.ui.action.ClickAction;

import java.util.function.Consumer;

public interface DialogButton {
    int DEFAULT_BUTTON = 0b1;

    static TextDialogButton createTextButton(int id, int behaviour, I18nItem label, Icon icon, Consumer<ClickAction> actionConsumer) {
        return NodeFlow.getApplication().getUIManager().getDialogManager().createTextButton(id, behaviour, label, icon, actionConsumer);
    }

    static CheckBoxDialogButton createCheckBoxButton(int id, int behaviour, I18nItem label, Consumer<ClickAction> actionConsumer) {
        return NodeFlow.getApplication().getUIManager().getDialogManager().createCheckBoxButton(id, behaviour, label, actionConsumer);
    }

    int getId();

    void setId(int id);

    int getBehaviour();

    void dispatchClick(ClickAction action);

    ButtonPeer createPeer();

    void setDisable(boolean disable);

    boolean isDisable();

    BooleanProperty impl_disableProperty();
}
