package thito.nodeflow.library.ui.layout.handler.child;

import javafx.scene.*;
import javafx.scene.layout.*;
import thito.nodeflow.library.ui.layout.*;

public class PaneChildHandler implements ComponentChildHandler<Pane> {

    public static final PaneChildHandler CHILD_HANDLER = new PaneChildHandler();

    @Override
    public void handleChild(Pane component, Object child) throws LayoutParserException {
        if (!component.getChildren().contains(child)) {
            component.getChildren().add((Node) child);
        }
    }

}

