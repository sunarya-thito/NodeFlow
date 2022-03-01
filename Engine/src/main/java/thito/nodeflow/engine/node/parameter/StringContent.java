package thito.nodeflow.engine.node.parameter;

import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;

public class StringContent implements ParameterContent {
    private final BorderPane content = new BorderPane();
    private final TextArea textField = new TextArea();

    public StringContent() {
        content.setCenter(textField);
        content.getStyleClass().add("string-content");
    }

    @Override
    public Node getNode() {
        return content;
    }

    public StringProperty valueProperty() {
        return textField.textProperty();
    }
}
