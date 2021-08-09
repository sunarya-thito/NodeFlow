package thito.nodeflow.library.ui.layout.handler.attribute;

import thito.nodeflow.library.ui.layout.*;
import thito.nodeflow.library.ui.layout.tag.*;

public class TagAttributeHandler implements ComponentAttributeHandler {
    @Override
    public boolean handle(String attribute, String value, Object component) throws LayoutParserException {
        if (component instanceof Tag) {
            ((Tag) component).getMap().put(attribute, value);
            return true;
        }
        return false;
    }
}
