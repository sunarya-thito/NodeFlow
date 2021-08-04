package thito.nodeflow.internal.node.parameter;

import javafx.beans.property.*;
import javafx.collections.*;
import javafx.geometry.Pos;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import thito.nodeflow.api.*;
import thito.nodeflow.api.ui.*;
import thito.nodeflow.api.ui.list.*;
import thito.nodeflow.internal.Toolkit;
import thito.nodeflow.internal.node.*;
import thito.nodeflow.internal.node.provider.*;
import thito.nodeflow.internal.ui.dialog.*;
import thito.nodeflow.internal.ui.list.*;
import thito.nodejfx.*;
import thito.nodejfx.parameter.*;
import thito.nodejfx.parameter.converter.*;
import thito.nodejfx.parameter.type.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

public class PreEnumParameter extends NodeParameter implements UserInputParameter<Field> {

    private ObjectProperty<Object> value = new SimpleObjectProperty<>();
    private ObjectProperty<TypeCaster<Field>> typeCaster = new SimpleObjectProperty<>();

    private Label fieldText;
    private Button input;
    private BorderPane box = new BorderPane();
    private Class<?> type;
    public PreEnumParameter(String fieldName, Class<?> javaClass) {
        IconedListHandler handler = ((IconedListImpl) NodeFlow.getApplication().getUIManager().getIconedList()).get(javaClass);
        fieldText = new Label(fieldName);
        fieldText.setTextFill(Color.WHITE);
        typeCaster.set(new FieldTypeCaster(javaClass));
        ObservableList<Field> values = FXCollections.observableArrayList(Arrays.stream(javaClass.getDeclaredFields()).filter(field -> field.getType().equals(javaClass)).collect(Collectors.toList()));
        values.add(0, null);
        input = new Button();
        BorderPane.setMargin(fieldText, new Insets(0, 20, 0, 0));
        BorderPane.setAlignment(fieldText, Pos.CENTER);
        BorderPane.setAlignment(input, Pos.CENTER_LEFT);
        box.setLeft(fieldText);
        box.setRight(input);
        getContainer().getChildren().add(box);
        getInputType().set(JavaParameterType.getCastableType(javaClass));
        getOutputType().set(JavaParameterType.getCastableType(javaClass));
        input.getStyleClass().add("enum-button");
        input.setOnAction(event -> {
            Dialogs.openEnumSelect(Toolkit.getWindow(this), javaClass, result -> {
                value.set(result);
            });
        });
        value.addListener((obs, old, val) -> {
            val = typeCaster.get().fromSafeObject(val);
            if (val instanceof Field) {
                input.setText(JavaNodeProviderCategory.capitalizeCamelCase(((Field) val).getName()));
                if (handler != null) {
                    IconedContent content = handler.getContent(val);
                    if (content != null) {
                        ImageView view = new ImageView();
                        Icon icon = content.getIcon();
                        if (icon == null) {
                            icon = NodeFlow.getApplication().getResourceManager().getIcon("missing-object");
                        }
                        view.imageProperty().bind(icon.impl_propertyPeer());
                        input.setGraphic(view);
                        return;
                    }
                } else {
                    input.setGraphic(null);
                }
            } else {
                input.setText("< NULL >");
                input.setGraphic(null);
            }
        });
        getUnmodifiableInputLinks().addListener((SetChangeListener<NodeParameter>) change -> {
            if (change.wasRemoved()) {
                valueProperty().unbind();
            }
            if (change.wasAdded()) {
                NodeParameter parameter = change.getElementAdded();
                if (parameter instanceof UserInputParameter) {
                    valueProperty().bind(((UserInputParameter) parameter).valueProperty());
                }
            }
            input.setDisable(!change.getSet().isEmpty());
        });
        getMultipleInputAssigner().set(false);
        getMultipleOutputAssigner().set(true);
    }

    @Override
    public Node getInputComponent() {
        return input;
    }

    public Class<?> getType() {
        return type;
    }

    @Override
    public void setName(String name) {
        fieldText.setText(name);
    }

    @Override
    public Label getLabel() {
        return fieldText;
    }

    @Override
    public BooleanProperty disableInputProperty() {
        return input.disableProperty();
    }

    @Override
    public ObjectProperty<Object> valueProperty() {
        return value;
    }

    @Override
    public ObjectProperty<TypeCaster<Field>> typeCaster() {
        return typeCaster;
    }
}
