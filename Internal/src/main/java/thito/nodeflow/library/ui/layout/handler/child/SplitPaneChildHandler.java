package thito.nodeflow.library.ui.layout.handler.child;

import javafx.scene.*;
import javafx.scene.control.*;
import thito.nodeflow.library.ui.layout.*;

public class SplitPaneChildHandler implements ComponentChildHandler<SplitPane> {
    @Override
    public void handleChild(SplitPane component, Object child) throws LayoutParserException {
        if (child instanceof Node) {
            component.getItems().add((Node) child);
        }
    }
}
