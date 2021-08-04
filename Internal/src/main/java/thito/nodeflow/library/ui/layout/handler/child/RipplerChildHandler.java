package thito.nodeflow.library.ui.layout.handler.child;

import com.jfoenix.controls.*;
import javafx.scene.*;
import thito.nodeflow.library.ui.layout.*;

public class RipplerChildHandler implements ComponentChildHandler<JFXRippler> {
    @Override
    public void handleChild(JFXRippler component, Object child) throws LayoutParserException {
        if (child instanceof Node) {
            component.setControl((Node) child);
        }
    }
}
