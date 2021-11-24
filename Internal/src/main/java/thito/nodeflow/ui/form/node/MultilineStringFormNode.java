package thito.nodeflow.ui.form.node;

import javafx.scene.*;
import javafx.scene.control.*;
import thito.nodeflow.ui.form.*;

public class MultilineStringFormNode implements FormNode<String> {
    private TextArea textArea = new TextArea();

    public MultilineStringFormNode() {
        textArea.getStyleClass().add("multiline-string-form-node");
    }

    @Override
    public Node getNode() {
        return textArea;
    }

    @Override
    public void initialize(FormProperty<String> property) {
        textArea.textProperty().bindBidirectional(property);
    }
}
