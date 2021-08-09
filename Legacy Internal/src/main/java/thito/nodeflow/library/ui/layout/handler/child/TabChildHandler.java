package thito.nodeflow.library.ui.layout.handler.child;

import javafx.scene.*;
import javafx.scene.control.*;
import thito.nodeflow.library.ui.layout.*;

public class TabChildHandler implements ComponentChildHandler<Tab> {
    @Override
    public void handleChild(Tab component, Object child) throws LayoutParserException {
        if (child instanceof Node) {
            component.setContent((Node) child);
        }
    }
}
