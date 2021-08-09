package thito.nodeflow.internal.ui.menu.handler;

import com.jfoenix.controls.*;
import javafx.beans.property.*;
import javafx.scene.*;
import thito.nodeflow.api.ui.menu.*;
import thito.nodeflow.api.ui.menu.handler.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.internal.ui.menu.type.*;

public class CheckBoxMenuItemHandlerImpl implements CheckBoxMenuItemHandler {
    private BooleanProperty selected = new SimpleBooleanProperty();
    private MenuItem item;

    public CheckBoxMenuItemHandlerImpl(MenuItem item) {
        this.item = item;
    }

    @Override
    public MenuItemType getType() {
        return CheckBoxMenuItemTypeImpl.INSTANCE;
    }

    @Override
    public Node impl_createPeer() {
        JFXCheckBox box = new JFXCheckBox();
        box.selectedProperty().bindBidirectional(selected);
        box.setMouseTransparent(true);
        Toolkit.style(box, "menu-item-button", "menu-checkbox-type");
        box.setOnAction(event -> {
            item.dispatch();
        });
        return box;
    }

    @Override
    public boolean isSelected() {
        return impl_selectedProperty().get();
    }

    @Override
    public void setSelected(boolean selected) {
        impl_selectedProperty().set(selected);
    }

    @Override
    public BooleanProperty impl_selectedProperty() {
        return selected;
    }

    @Override
    public void handleDispatch() {
        setSelected(!isSelected());
    }
}
