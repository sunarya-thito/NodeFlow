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

import java.util.function.*;

public class ToolButtonImpl implements ToolButton, ToolControl {

    protected BorderPane pane;
    protected I18nItem tooltip;
    protected Icon icon;
    protected Consumer<ClickAction.MouseButton> action;
    protected BooleanProperty disable = new SimpleBooleanProperty();

    public ToolButtonImpl(I18nItem tooltip, Icon icon, Consumer<ClickAction.MouseButton> action) {
        this.tooltip = tooltip;
        this.icon = icon;
        this.action = action;
    }

    @Override
    public boolean isDisabled() {
        return disable.get();
    }

    @Override
    public void setDisabled(boolean disabled) {
        disable.set(disabled);
    }

    @Override
    public BooleanProperty impl_disableProperty() {
        return disable;
    }

    @Override
    public I18nItem getTooltip() {
        return tooltip;
    }

    @Override
    public Icon getIcon() {
        return icon;
    }

    @Override
    public void dispatchClick(ClickAction.MouseButton button) {
        if (action != null) {
            action.accept(button);
        }
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
            pane.getStyleClass().add("tool-button");
            ImagePane imagePane = new ImagePane();
            imagePane.imageProperty().bind(icon.impl_propertyPeer());
            pane.setCenter(imagePane);
        }
        return pane;
    }
}
