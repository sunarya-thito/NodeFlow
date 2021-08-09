package thito.nodeflow.internal.node.parameter;

import javafx.beans.property.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.chat.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.ui.dialog.Dialog;
import thito.nodeflow.api.ui.dialog.button.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.internal.ui.editor.*;
import thito.nodeflow.minecraft.*;
import thito.nodejfx.*;
import thito.nodejfx.parameter.*;
import thito.nodejfx.parameter.converter.*;
import thito.nodejfx.parameter.type.*;

public class BaseComponentParameter extends NodeParameter implements UserInputParameter<BaseComponent[]> {
    private Label fieldText;
    private ComplexColorizer complexColorizer;
    private BorderPane box = new BorderPane();
    private ObjectProperty<Object> value = new SimpleObjectProperty<>();
    private ObjectProperty<BaseComponent[]> components = new SimpleObjectProperty<>(new BaseComponent[] {new TextComponent()});
    private ObjectProperty<TypeCaster<BaseComponent[]>> typeCaster = new SimpleObjectProperty<>(new ComponentTypeCaster());

    static class ComponentTypeCaster implements TypeCaster<BaseComponent[]> {
        @Override
        public BaseComponent[] fromSafeObject(Object obj) {
            if (obj instanceof String) {
                return ComponentSerializer.parse((String) obj);
            }
            return new BaseComponent[0];
        }

        @Override
        public Object toSafeObject(BaseComponent[] obj) {
            return ComponentSerializer.toString(obj);
        }
    }

    public BaseComponentParameter(String fieldName) {
        fieldText = new Label(fieldName);
        fieldText.setTextFill(Color.WHITE);
        complexColorizer = new ComplexColorizer();
        complexColorizer.componentsProperty().bind(components);
//        input.getStyleClass().add("text-field-label");
        complexColorizer.getStyleClass().add("component-field-label");
        complexColorizer.setOnMouseClicked(event -> {
            ChatComponentDialogContent content = new ChatComponentDialogContent(components);
            thito.nodeflow.api.ui.dialog.Dialog dialog = Dialog.createDialog(content, 0, DialogButton.createTextButton(0, 0, I18n.$("button-ok"), null, mouseEvent -> {
                mouseEvent.close();
            }));
            dialog.open(Toolkit.getWindow(complexColorizer));
        });
        getContainer().getChildren().add(box);
        BorderPane.setMargin(fieldText, new Insets(0, 20, 0, 0));
        BorderPane.setAlignment(fieldText, Pos.CENTER);
        BorderPane.setAlignment(complexColorizer, Pos.CENTER_LEFT);
        box.setLeft(fieldText);
        box.setCenter(complexColorizer);
        getInputType().set(JavaParameterType.getType(String.class));
        getOutputType().set(JavaParameterType.getType(String.class));
        TypeCaster.bindBidirectional(value, components, typeCaster);
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
            complexColorizer.setDisable(!change.getSet().isEmpty());
        });
        getMultipleInputAssigner().set(false);
        getMultipleOutputAssigner().set(true);
    }

    @Override
    public Node getInputComponent() {
        return complexColorizer;
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
        return complexColorizer.disableProperty();
    }

    @Override
    public ObjectProperty<TypeCaster<BaseComponent[]>> typeCaster() {
        return typeCaster;
    }

    @Override
    public ObjectProperty<Object> valueProperty() {
        return value;
    }

    public Label getFieldText() {
        return fieldText;
    }

}
