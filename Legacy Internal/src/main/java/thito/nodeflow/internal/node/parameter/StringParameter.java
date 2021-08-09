package thito.nodeflow.internal.node.parameter;

import javafx.beans.property.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.ui.dialog.Dialog;
import thito.nodeflow.api.ui.dialog.button.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.internal.ui.editor.*;
import thito.nodejfx.*;
import thito.nodejfx.parameter.*;
import thito.nodejfx.parameter.converter.*;
import thito.nodejfx.parameter.type.*;

public class StringParameter extends NodeParameter implements UserInputParameter<String> {
    private Label fieldText;
    private Label input;
    private BorderPane box = new BorderPane();
    private ObjectProperty<Object> value = new SimpleObjectProperty<>();
    private ObjectProperty<TypeCaster<String>> typeCaster = new SimpleObjectProperty<>(TypeCaster.STRING_TYPE_CASTER);

    public StringParameter(String fieldName) {
        fieldText = new Label(fieldName);
        fieldText.setTextFill(Color.WHITE);
        input = new Label();
        input.getStyleClass().add("text-field-label");
        input.setOnMouseClicked(event -> {
            TextDialogContent content = new TextDialogContent(input.textProperty());
            Dialog dialog = Dialog.createDialog(content, 0, DialogButton.createTextButton(0, 0, I18n.$("button-ok"), null, mouseEvent -> {
                mouseEvent.close();
            }));
            dialog.open(Toolkit.getWindow(input));
        });
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

    public Label getInput() {
        return input;
    }
}
