package thito.nodeflow.ui.docker;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import thito.nodeflow.language.I18n;

import java.util.function.Consumer;

public abstract class DockNode extends BorderPane {
    private final BooleanProperty tabFocused = new SimpleBooleanProperty();
    private final ObjectProperty<DockerTab> tab = new SimpleObjectProperty<>();

    public BooleanProperty tabFocusedProperty() {
        return tabFocused;
    }

    public ObjectProperty<DockerTab> tabProperty() {
        return tab;
    }

    public abstract I18n titleProperty();
    public abstract ObjectProperty<Image> iconProperty();
    public void onCloseAttempt(Consumer<Boolean> resultConsumer) {
        resultConsumer.accept(true);
    }
    public abstract DockNodeState createState();
    public abstract DockerComponent getComponent();
}
