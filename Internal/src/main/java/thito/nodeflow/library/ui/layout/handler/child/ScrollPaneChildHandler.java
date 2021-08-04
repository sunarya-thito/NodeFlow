package thito.nodeflow.library.ui.layout.handler.child;

import javafx.scene.*;
import javafx.scene.control.*;
import thito.nodeflow.library.ui.layout.*;

public class ScrollPaneChildHandler implements ComponentChildHandler<ScrollPane> {
    @Override
    public void handleChild(ScrollPane component, Object child) throws LayoutParserException {
        component.setContent((Node) child);
    }
}
