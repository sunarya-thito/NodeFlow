package thito.nodeflow.library.ui;

import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import thito.nodeflow.internal.*;

public class AddButton extends Pane {
    private Rectangle rect1 = new Rectangle();
    private Rectangle rect2 = new Rectangle();
    private BooleanProperty hovered = new SimpleBooleanProperty();

    public AddButton() {
        Pseudos.install(this, Pseudos.HOVERED, hovered);
        getChildren().addAll(rect1, rect2);

        rect1.widthProperty().bind(widthProperty().multiply(15 / 100d));
        rect2.heightProperty().bind(heightProperty().multiply(15 / 100d));

        rect1.heightProperty().bind(heightProperty().subtract(heightProperty().multiply(30 / 100d)));
        rect2.widthProperty().bind(widthProperty().subtract(widthProperty().multiply(30 / 100d)));

        rect1.layoutXProperty().bind(widthProperty().subtract(rect1.widthProperty()).divide(2));
        rect1.layoutYProperty().bind(heightProperty().multiply(15/100d));

        rect2.layoutXProperty().bind(widthProperty().multiply(15/100d));
        rect2.layoutYProperty().bind(heightProperty().subtract(rect2.heightProperty()).divide(2));

        setBackground(new Background(new BackgroundFill(null, new CornerRadii(100, true), null)));
        Toolkit.style(rect1, "add-button-cross-fill");
        rect2.fillProperty().bind(rect1.fillProperty());

        setMinHeight(20);
        setMinWidth(20);

        maxHeightProperty().bind(Bindings.min(heightProperty(), widthProperty()));
        maxWidthProperty().bind(maxHeightProperty());

        setOnMouseEntered(event -> {
            hovered.set(true);
        });

        setOnMouseExited(event -> {
            hovered.set(false);
        });
        setOpacity(0);
        onMouseClickedProperty().addListener((obs, old, val) -> {
            setOpacity(val == null ? 0 : 1);
        });
    }

}
