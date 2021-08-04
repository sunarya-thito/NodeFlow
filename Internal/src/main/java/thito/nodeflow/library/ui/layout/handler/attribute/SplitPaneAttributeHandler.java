package thito.nodeflow.library.ui.layout.handler.attribute;

import javafx.scene.*;
import javafx.scene.control.*;
import thito.nodeflow.library.ui.layout.*;

public class SplitPaneAttributeHandler implements ComponentAttributeHandler {
    @Override
    public boolean handle(String attribute, String value, Object component) throws LayoutParserException {
        if (component instanceof Node) {
            Parent parent = ((Node) component).getParent();
            if (parent instanceof SplitPane) {
                SplitPane splitPane = (SplitPane) parent;
                switch (attribute) {
                    case "splitpane.resizewithparent":
                        SplitPane.setResizableWithParent((Node) component, Boolean.parseBoolean(value));
                        return true;
                    case "splitpane.divider":
                        int index = splitPane.getItems().indexOf(component);
                        if (index >= 0) {
                            splitPane.setDividerPosition(index, LayoutHelper.parseDouble(value));
                            return true;
                        }
                }
            }
        }
        return false;
    }
}
