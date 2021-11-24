package thito.nodeflow.ui.editor;

import javafx.scene.layout.Pane;
import thito.nodeflow.ui.Component;
import thito.nodeflow.ui.FormPanelSkin;
import thito.nodeflow.ui.Skin;

public class EditorPluginPanelSkin extends Skin {

    private EditorSkin editor;

    @Component("container")
    Pane container;

    public EditorPluginPanelSkin(EditorSkin editor) {
        this.editor = editor;
    }

    @Override
    protected void onLayoutLoaded() {
    }
}
