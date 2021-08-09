package thito.nodeflow.library.ui.layout.handler.child;

import javafx.scene.*;
import thito.nodeflow.library.ui.layout.*;

public class GroupChildHandler implements ComponentChildHandler<Group> {
    @Override
    public void handleChild(Group component, Object child) throws LayoutParserException {
        if (!component.getChildren().contains(child)) {
            component.getChildren().add((Node) child);
        }
    }
}
