package thito.nodeflow.internal.editor.menu;

import javafx.beans.property.*;
import javafx.collections.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import thito.nodeflow.api.editor.menu.*;
import thito.nodeflow.api.ui.*;
import thito.nodeflow.internal.*;

public class ToolChoiceImpl<T> implements ToolChoice<T> {

    private T[] choices;
    private ObjectProperty<T> chosen;
    private BooleanProperty disable = new SimpleBooleanProperty();

    public ToolChoiceImpl(ObjectProperty<T> chosen, T... choices) {
        this.choices = choices;
        this.chosen = chosen;
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
    public T[] getChoices() {
        return choices;
    }

    @Override
    public T getChosen() {
        return chosen.get();
    }

    @Override
    public ObjectProperty<T> impl_chosenProperty() {
        return chosen;
    }

    private ComboBox<T> comboBox;
    private StackPane pane;
    @Override
    public Node impl_getPeer() {
        if (pane == null) {
            comboBox = new ComboBox<>(FXCollections.observableArrayList(choices));
            comboBox.setCellFactory(param -> new ListCell<T>() {
                @Override
                protected void updateItem(T item, boolean empty) {
                    if (item instanceof Iconable) {
                        ImageView view = new ImageView();
                        view.imageProperty().bind(((Iconable) item).getIcon().impl_propertyPeer());
                        setGraphic(view);
                    }
                    if (item != null) {
                        setText(Toolkit.STRING_CONVERTER.toString(item));
                    }
                    super.updateItem(item, empty);
                }
            });
            comboBox.setButtonCell(new ListCell<T>() {
                @Override
                protected void updateItem(T item, boolean empty) {
                    if (item instanceof Iconable) {
                        ImageView view = new ImageView();
                        view.imageProperty().bind(((Iconable) item).getIcon().impl_propertyPeer());
                        setGraphic(view);
                    }
                    if (item != null) {
                        setText(Toolkit.STRING_CONVERTER.toString(item));
                    }
                    super.updateItem(item, empty);
                }
            });
            pane = new StackPane(comboBox);
            comboBox.setConverter(Toolkit.STRING_CONVERTER);
            Toolkit.style(pane, "tool-choice");
            comboBox.valueProperty().bindBidirectional(chosen);
        }
        return pane;
    }
}
