package thito.nodeflow.installer;

import javafx.beans.property.*;
import javafx.scene.layout.*;

public abstract class Wizard extends VBox {
    private final StringProperty title = new SimpleStringProperty("");
    private final StringProperty backLabel = new SimpleStringProperty("Back (_B)");
    private final StringProperty nextLabel = new SimpleStringProperty("Next (_N)");
    private final StringProperty cancelLabel = new SimpleStringProperty("Cancel (_C)");
    private final BooleanProperty cancellable = new SimpleBooleanProperty(true);
    private final BooleanProperty disableNext = new SimpleBooleanProperty(false);
    private final BooleanProperty disableBack = new SimpleBooleanProperty(false);
    private final BooleanProperty active = new SimpleBooleanProperty(false);

    public Wizard() {
        getStyleClass().add("wizard-container");
    }

    public StringProperty cancelLabelProperty() {
        return cancelLabel;
    }

    public StringProperty backLabelProperty() {
        return backLabel;
    }

    public StringProperty nextLabelProperty() {
        return nextLabel;
    }

    public StringProperty titleProperty() {
        return title;
    }

    public BooleanProperty cancellableProperty() {
        return cancellable;
    }

    public BooleanProperty disableBackProperty() {
        return disableBack;
    }

    public BooleanProperty disableNextProperty() {
        return disableNext;
    }

    public BooleanProperty activeProperty() {
        return active;
    }
}
