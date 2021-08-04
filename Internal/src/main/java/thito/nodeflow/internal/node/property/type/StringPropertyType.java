package thito.nodeflow.internal.node.property.type;

import javafx.beans.property.*;
import javafx.scene.*;
import javafx.scene.control.*;
import thito.nodeflow.api.project.property.*;

public class StringPropertyType implements ComponentPropertyType<String> {
    public static final StringPropertyType TYPE = new StringPropertyType();
    @Override
    public ComponentPropertyHandler<String> createHandler(ComponentProperty property) {
        return new ComponentPropertyHandler<String>() {
            private TextField field = new TextField();
            private BooleanProperty disable = new SimpleBooleanProperty();

            {
                field.getStyleClass().add("component-property-string");
                field.textProperty().bindBidirectional(property.valueProperty());
                field.editableProperty().bind(disable.not());
            }

            @Override
            public BooleanProperty disableProperty() {
                return disable;
            }

            @Override
            public Node impl_getPeer() {
                return field;
            }
        };
    }
}
