package thito.nodeflow.internal.ui.menu.handler;

import javafx.beans.property.*;
import javafx.scene.*;
import javafx.scene.control.RadioButton;
import thito.nodeflow.api.ui.*;
import thito.nodeflow.api.ui.menu.MenuItem;
import thito.nodeflow.api.ui.menu.*;
import thito.nodeflow.api.ui.menu.handler.*;
import thito.nodeflow.internal.*;

public class RadioButtonMenuItemHandlerImpl implements RadioButtonMenuItemHandler {
    private MenuItem item;
    private RadioButtonGroup group;

    private BooleanProperty selected = new SimpleBooleanProperty();

    public RadioButtonMenuItemHandlerImpl(MenuItem item) {
        this.item = item;
        selected.addListener((obs, old, val) -> {
            if (val) {
                if (group != null) {
                    group.attemptSelect(this);
                }
            } else {
                if (group != null) {
                    group.attemptUnSelect(this);
                }
            }
        });
    }

    @Override
    public BooleanProperty impl_selectedProperty() {
        return selected;
    }

    @Override
    public boolean isSelected() {
        return selected.get();
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    @Override
    public RadioButtonGroup getGroup() {
        return group;
    }

    @Override
    public void setGroup(RadioButtonGroup group) {
        this.group = group;
        if (selected.get() && group != null) {
            group.attemptSelect(this);
        }
    }

    @Override
    public MenuItemType getType() {
        return MenuItemType.RADIO_BUTTON_TYPE;
    }

    @Override
    public Node impl_createPeer() {
        RadioButton radioButton = new RadioButton();
        radioButton.setMouseTransparent(true);
        radioButton.selectedProperty().bindBidirectional(selected);
        Toolkit.style(radioButton, "menu-item-button", "menu-radio-button-type");
        return radioButton;
    }

    @Override
    public void handleDispatch() {
        setSelected(!isSelected());
    }
}
