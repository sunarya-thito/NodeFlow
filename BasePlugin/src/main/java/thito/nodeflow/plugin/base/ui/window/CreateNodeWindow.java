package thito.nodeflow.plugin.base.ui.window;

import thito.nodeflow.ui.StandardWindow;
import thito.nodeflow.plugin.base.ui.CreateNodeSkin;

public class CreateNodeWindow extends StandardWindow {

    @Override
    protected void initializeWindow() {
        super.initializeWindow();
        contentProperty().set(new CreateNodeSkin());
    }
}
