package thito.nodeflow.internal.node.parameter;

import javafx.beans.property.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import thito.nodeflow.api.node.*;
import thito.nodeflow.api.project.*;
import thito.nodeflow.library.ui.*;
import thito.nodejfx.*;
import thito.nodejfx.parameter.*;
import thito.nodejfx.parameter.converter.*;
import thito.nodejfx.parameter.type.*;

import java.lang.reflect.*;

public class StringCompleterParameter extends NodeParameter implements UserInputParameter<String> {
    private Label fieldText;
    private CompletableTextField input;
    private BorderPane box = new BorderPane();
    private ObjectProperty<Object> value = new SimpleObjectProperty<>();
    private ObjectProperty<TypeCaster<String>> typeCaster = new SimpleObjectProperty<>(TypeCaster.STRING_TYPE_CASTER);

    public StringCompleterParameter(String fieldName, Project project, MethodParameterCompleter completer, Method method, Parameter parameter) {
        fieldText = new Label(fieldName);
        fieldText.setTextFill(Color.WHITE);
        input = new CompletableTextField(completer, project, method, parameter);
        input.getStyleClass().add("text-field-label");
        getContainer().getChildren().add(box);
        BorderPane.setMargin(fieldText, new Insets(0, 20, 0, 0));
        BorderPane.setAlignment(fieldText, Pos.CENTER);
        BorderPane.setAlignment(input, Pos.CENTER_LEFT);
        box.setLeft(fieldText);
        box.setCenter(input);
        getInputType().set(JavaParameterType.getType(String.class));
        getOutputType().set(JavaParameterType.getType(String.class));
        TypeCaster.bindBidirectional(value, input.textProperty(), typeCaster);
        getUnmodifiableInputLinks().addListener((SetChangeListener<NodeParameter>) change -> {
            if (change.wasRemoved()) {
                valueProperty().unbind();
            }
            if (change.wasAdded()) {
                NodeParameter param = change.getElementAdded();
                if (param instanceof UserInputParameter) {
                    valueProperty().bind(((UserInputParameter) param).valueProperty());
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
    public ObjectProperty<TypeCaster<String>> typeCaster() {
        return typeCaster;
    }

    @Override
    public ObjectProperty<Object> valueProperty() {
        return value;
    }

    public Label getFieldText() {
        return fieldText;
    }

//    public Label getInput() {
//        return input;
//    }
}
