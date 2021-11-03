package thito.nodeflow.internal.ui.editor;

import javafx.scene.layout.Pane;
import thito.nodeflow.internal.ui.Component;
import thito.nodeflow.internal.ui.FormPanelSkin;
import thito.nodeflow.internal.ui.Skin;

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
