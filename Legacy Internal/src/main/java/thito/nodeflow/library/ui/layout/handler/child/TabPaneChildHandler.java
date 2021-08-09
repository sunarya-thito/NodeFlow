package thito.nodeflow.library.ui.layout.handler.child;

import com.jfoenix.controls.*;
import javafx.scene.control.*;
import thito.nodeflow.library.ui.layout.*;

public class TabPaneChildHandler implements ComponentChildHandler<JFXTabPane> {
    @Override
    public void handleChild(JFXTabPane component, Object child) throws LayoutParserException {
        if (child instanceof Tab) {
            component.getTabs().add((Tab) child);
        }
    }
}
