package thito.nodeflow.internal.node.parameter;

import javafx.beans.property.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.internal.ui.dialog.*;
import thito.nodejfx.*;
import thito.nodejfx.parameter.*;
import thito.nodejfx.parameter.converter.*;
import thito.nodejfx.parameter.type.*;

public class CharacterParameter extends NodeParameter implements UserInputParameter<Character> {

    private ObjectProperty<Object> value = new SimpleObjectProperty<>();
    private ObjectProperty<TypeCaster<Character>> typeCaster = new SimpleObjectProperty<>();

    private Label fieldText;
    private Button input;
    private BorderPane box = new BorderPane();
    public CharacterParameter(String fieldName) {
        fieldText = new Label(fieldName);
        fieldText.setTextFill(Color.WHITE);
        typeCaster.set(new TypeCaster<Character>() {
            @Override
            public Character fromSafeObject(Object obj) {
                if (obj instanceof Number) {
                    obj = (char) ((Number) obj).intValue();
                }
                if (obj instanceof Character) {
                    return (Character) obj;
                }
                return null;
            }

            @Override
            public Object toSafeObject(Character obj) {
                return (int) obj.charValue();
            }
        });
        input = new Button();
        BorderPane.setMargin(fieldText, new Insets(0, 20, 0, 0));
        BorderPane.setAlignment(fieldText, Pos.CENTER);
        BorderPane.setAlignment(input, Pos.CENTER_LEFT);
        box.setLeft(fieldText);
        box.setRight(input);
        getContainer().getChildren().add(box);
        getInputType().set(JavaParameterType.getCastableType(Character.class));
        getOutputType().set(JavaParameterType.getCastableType(Character.class));
        input.getStyleClass().add("character-button");
        input.setOnAction(event -> {
            Dialogs.openCharSelect(Toolkit.getWindow(this), result -> {
                value.set(result);
            });
        });
        value.addListener((obs, old, val) -> {
            val = typeCaster.get().fromSafeObject(val);
            if (val instanceof Character) {
                input.setText(val.toString());
            } else {
                if (value.isBound()) {
                    input.setText("< ? >");
                } else {
                    input.setText("< NULL >");
                }
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
    public ObjectProperty<TypeCaster<Character>> typeCaster() {
        return typeCaster;
    }
}
