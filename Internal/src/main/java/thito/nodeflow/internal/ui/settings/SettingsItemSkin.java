package thito.nodeflow.internal.ui.settings;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.internal.settings.*;
import thito.nodeflow.library.ui.Skin;
import thito.nodeflow.library.ui.*;

public class SettingsItemSkin extends Skin {

    @Component("name")
    Label name;

    @Component("item")
    BorderPane content;

    private SettingsProperty<?> item;

    public SettingsItemSkin(SettingsProperty<?> item) {
        this.item = item;
    }

    @Override
    protected void onLayoutLoaded() {
        name.textProperty().bind(item.displayNameProperty());
        SettingsNodeFactory settingsNodeFactory = NodeFlow.getInstance().getSettingsManager().getNodeFactory(item.getType());
        SettingsNode node = settingsNodeFactory.createNode(item);
        content.setCenter(node.getNode());
    }
}
