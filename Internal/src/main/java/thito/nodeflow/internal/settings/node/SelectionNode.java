package thito.nodeflow.internal.settings.node;

import javafx.beans.property.*;
import javafx.collections.*;
import javafx.scene.*;
import javafx.scene.control.*;
import thito.nodeflow.internal.settings.*;
import thito.nodeflow.library.application.*;
import thito.nodeflow.library.language.*;
import thito.nodeflow.library.ui.*;

public class SelectionNode<T> extends SettingsNode<T> {

    public static class LanguageFactory implements SettingsNodeFactory<Language> {
        @Override
        public SettingsNode<Language> createNode(SettingsProperty<Language> item) {
            return new SelectionNode<>(item, FXCollections.observableArrayList(ApplicationResources.getInstance().getAvailableLanguages()));
        }
    }

    public static class ThemeFactory implements SettingsNodeFactory<Theme> {
        @Override
        public SettingsNode<Theme> createNode(SettingsProperty<Theme> item) {
            return new SelectionNode<>(item, FXCollections.observableArrayList(ApplicationResources.getInstance().getAvailableThemes()));
        }
    }

    private ComboBox<T> comboBox;
    public SelectionNode(SettingsProperty<T> item, ObservableList<T> selections) {
        super(item);
        comboBox = new ComboBox<>(selections);
        comboBox.setValue(item.get());
        comboBox.getStyleClass().add("settings-list");
    }

    @Override
    public void apply() {
        getItem().set(comboBox.getValue());
    }

    @Override
    public Node getNode() {
        return comboBox;
    }
}
