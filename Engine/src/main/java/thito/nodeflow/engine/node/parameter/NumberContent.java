package thito.nodeflow.engine.node.parameter;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.BorderPane;

public class NumberContent implements ParameterContent {
    private final BorderPane content = new BorderPane();
    private final Spinner<Double> spinner = new Spinner<>();

    public NumberContent() {
        content.setCenter(spinner);
        spinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(Double.MIN_VALUE, Double.MAX_VALUE));
        content.getStyleClass().add("number-content");
    }

    @Override
    public Node getNode() {
        return content;
    }

    public ObjectProperty<Double> valueProperty() {
        return spinner.getValueFactory().valueProperty();
    }
}
