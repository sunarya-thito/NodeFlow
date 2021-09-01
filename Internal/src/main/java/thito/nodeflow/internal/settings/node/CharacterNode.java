package thito.nodeflow.internal.settings.node;

import javafx.scene.*;
import javafx.scene.control.*;
import thito.nodeflow.internal.settings.*;

public class CharacterNode extends SettingsNode<Character> {
    public static class Factory implements SettingsNodeFactory<Character> {
        @Override
        public SettingsNode<Character> createNode(SettingsProperty<Character> item) {
            return new CharacterNode(item);
        }
    }
    private TextField textField;

    public CharacterNode(SettingsProperty<Character> item) {
        super(item);
        textField = new TextField();
        textField.setText(Character.toString(item.get()));
        textField.getStyleClass().add("settings-character");
        textField.textProperty().addListener((obs, old, val) -> {
            if (val.length() > 1) {
                textField.setText(Character.toString(val.charAt(0)));
            }
            hasChangedProperty().set(val.isEmpty() ? item.get() == null : val.charAt(0) == item.get());
        });
    }

    @Override
    public Node getNode() {
        return textField;
    }

    @Override
    public void apply() {
        String text = textField.getText();
        getItem().set(text.length() >= 1 ? text.charAt(0) : null);
    }
}
