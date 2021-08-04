package thito.nodeflow.library.ui.layout.handler.child;

import thito.nodeflow.library.ui.layout.*;
import thito.nodeflow.library.ui.layout.tag.*;

public class TagChildHandler implements ComponentChildHandler<Tag> {
    @Override
    public void handleChild(Tag component, Object child) throws LayoutParserException {
        component.getChildren().add(child);
    }
}
