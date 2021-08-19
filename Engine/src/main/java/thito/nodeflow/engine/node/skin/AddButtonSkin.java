package thito.nodeflow.engine.node.skin;

import javafx.beans.binding.*;
import javafx.css.*;
import javafx.scene.shape.*;

public class AddButtonSkin extends Skin {
    private static final CssMetaData<AddButtonSkin, Number> meta = meta("-fx-sign-thickness", StyleConverter.getSizeConverter(), 2, AddButtonSkin::signThicknessProperty);
    private final StyleableDoubleProperty signThickness = new SimpleStyleableDoubleProperty(meta, 2d);
    public AddButtonSkin() {
        Rectangle v = skin(new Rectangle(), "vertical-sign");
        Rectangle h = skin(new Rectangle(), "horizontal-sign");
        v.widthProperty().bind(signThickness);
        h.heightProperty().bind(signThickness);
        Circle shape = skin(new Circle(), "background-sign");
        v.layoutXProperty().bind(shape.radiusProperty().subtract(signThickness.divide(2)));
        h.layoutYProperty().bind(shape.radiusProperty().subtract(signThickness.divide(2)));
        shape.radiusProperty().bind(Bindings.min(widthProperty(), heightProperty()).divide(2));
        getChildren().addAll(shape, v, h);
    }

    public StyleableDoubleProperty signThicknessProperty() {
        return signThickness;
    }
}
