package thito.nodeflow.internal.editor.menu;

import javafx.beans.property.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import thito.nodeflow.api.editor.menu.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.ui.*;
import thito.nodeflow.api.ui.action.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.library.ui.*;

public class ToolRadioImpl extends ToolButtonImpl implements ToolRadio {
    private BooleanProperty selected = new SimpleBooleanProperty();
    private ObjectProperty<RadioButtonGroup> group = new SimpleObjectProperty<>();
    public ToolRadioImpl(I18nItem tooltip, Icon icon, boolean toggleable) {
        super(tooltip, icon, null);
        action = mouseButton -> {
            if (toggleable) {
                selected.set(!selected.get());
            } else {
                selected.set(true);
            }
        };
        selected.addListener((obs, old, val) -> {
            if (val) {
                if (group.get() != null) {
                    group.get().attemptSelect(this);
                }
            } else {
                if (group.get() != null) {
                    group.get().attemptUnSelect(this);
                }
            }
        });
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
    public RadioButtonGroup getGroup() {
        return group.get();
    }

    @Override
    public void setGroup(RadioButtonGroup group) {
        this.group.set(group);
    }

    @Override
    public Node impl_getPeer() {
        if (pane == null) {
            pane = new BorderPane();
            pane.disableProperty().bindBidirectional(disable);
            pane.setOnMouseClicked(event -> {
                dispatchClick(event.getButton() == MouseButton.PRIMARY ? ClickAction.MouseButton.LEFT :
                        event.getButton() == MouseButton.SECONDARY ? ClickAction.MouseButton.RIGHT : ClickAction.MouseButton.MIDDLE);
            });
            Tooltip tip = new Tooltip();
            tip.textProperty().bind(this.tooltip.stringBinding());
            Toolkit.install(pane, tip);
            Pseudos.install(pane, Pseudos.SELECTED, selected);
            pane.getStyleClass().addAll("tool-button", "tool-radio");
            ImagePane imagePane = new ImagePane();
            imagePane.imageProperty().bind(icon.impl_propertyPeer());
            pane.setCenter(imagePane);
        }
        return pane;
    }
}
