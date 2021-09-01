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

    @Component("full-item")
    BorderPane contentFull;

    private SettingsProperty<?> item;
    private SettingsSkin parent;

    public SettingsItemSkin(SettingsSkin parent, SettingsProperty<?> item) {
        this.parent = parent;
        this.item = item;
    }

    private SettingsNode settingsNode;

    @Override
    protected void onLayoutLoaded() {
        name.textProperty().bind(item.displayNameProperty());
        SettingsNodeFactory settingsNodeFactory = NodeFlow.getInstance().getSettingsManager().getNodeFactory(item.getType());
        settingsNode = settingsNodeFactory.createNode(item);
        settingsNode.hasChangedProperty().addListener((obs, old, val) -> {
            if (val) {
                parent.getChangedList().add(this);
            } else {
                parent.getChangedList().remove(this);
            }
        });
        if (settingsNode instanceof SettingsNodePane) {
            // specialize SettingsNodePane to have full viewport of the settings item row pane
            ((Pane) name.getParent()).getChildren().remove(name);
            ((Pane) content.getParent()).getChildren().remove(content);
            contentFull.setCenter(settingsNode.getNode());
        } else {
            ((Pane) contentFull.getParent()).getChildren().remove(contentFull);
            content.setCenter(settingsNode.getNode());
        }
    }

    public SettingsNode getSettingsNode() {
        return settingsNode;
    }
}
