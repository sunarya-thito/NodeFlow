package thito.nodeflow.internal.node.property.type;

import javafx.beans.property.*;
import javafx.collections.*;
import javafx.scene.*;
import javafx.scene.control.*;
import thito.nodeflow.api.project.property.*;

public class EnumPropertyType<T extends Enum<T>> implements ComponentPropertyType<T> {
    private Class<T> clazz;

    public EnumPropertyType(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public ComponentPropertyHandler<T> createHandler(ComponentProperty property) {
        return new ComponentPropertyHandler<T>() {

            private ComboBox<Enum<T>> comboBox = new ComboBox<>(FXCollections.observableArrayList(clazz.getEnumConstants()));

            {
                comboBox.getStyleClass().add("component-property-enum");
                comboBox.valueProperty().bindBidirectional(property.valueProperty());
            }

            @Override
            public BooleanProperty disableProperty() {
                return comboBox.disableProperty();
            }

            @Override
            public Node impl_getPeer() {
                return comboBox;
            }
        };
    }
}
