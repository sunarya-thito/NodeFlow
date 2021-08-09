package thito.nodeflow.internal.ui.dialog.button;

import javafx.beans.property.*;
import thito.nodeflow.api.ui.action.*;
import thito.nodeflow.api.ui.dialog.button.*;

import java.util.function.*;

public abstract class AbstractDialogButton implements DialogButton {
    private int id;
    private int behaviour;
    private Consumer<ClickAction> action;

    private BooleanProperty disable = new SimpleBooleanProperty();

    public AbstractDialogButton(int id, int behaviour, Consumer<ClickAction> action) {
        this.id = id;
        this.behaviour = behaviour;
        this.action = action;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getBehaviour() {
        return behaviour;
    }

    @Override
    public void dispatchClick(ClickAction action) {
        if (this.action != null) {
            this.action.accept(action);
        }
    }

    @Override
    public void setDisable(boolean disable) {
        impl_disableProperty().set(disable);
    }

    @Override
    public boolean isDisable() {
        return impl_disableProperty().get();
    }

    @Override
    public BooleanProperty impl_disableProperty() {
        return disable;
    }
}
