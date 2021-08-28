package thito.nodeflow.library.ui.form.node;

import javafx.scene.*;
import javafx.scene.control.*;
import thito.nodeflow.library.ui.form.*;

public class StringFormNode implements FormNode<String> {

    private TextField textField = new TextField();

    public StringFormNode() {
        textField.getStyleClass().add("string-form-node");
    }

    @Override
    public Node getNode() {
        return textField;
    }

    @Override
    public void initialize(FormProperty<String> property) {
        textField.textProperty().bindBidirectional(property);
    }

}
