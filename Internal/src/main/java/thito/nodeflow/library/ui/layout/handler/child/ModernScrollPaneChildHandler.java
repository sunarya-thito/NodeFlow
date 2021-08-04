package thito.nodeflow.library.ui.layout.handler.child;

import javafx.scene.*;
import thito.nodeflow.library.ui.*;
import thito.nodeflow.library.ui.layout.*;

public class ModernScrollPaneChildHandler implements ComponentChildHandler<ModernScrollPane> {
    @Override
    public void handleChild(ModernScrollPane component, Object child) throws LayoutParserException {
        component.setContent((Node) child);
    }
}
