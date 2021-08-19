package thito.nodeflow.engine.node;

import javafx.beans.property.*;
import javafx.collections.*;
import javafx.scene.Node;
import javafx.scene.paint.*;
import thito.nodeflow.engine.node.style.*;
import thito.nodeflow.engine.node.util.*;

public interface LinkStyle {
    LinkStyle
            LINE = new LineLinkStyle(),
            PATH = new PathLinkStyle(),
            PIPE = new PipeLinkStyle(),
            CABLE = new CableLinkStyle();
    Handler createHandler(NodeLink link);
    interface Handler {
        void update();
        ObjectProperty<Paint> fillProperty();
        BooleanProperty highlightProperty();
        ObservableSet<Object> requestHighlight();
        DoubleProperty sourceXProperty();
        DoubleProperty sourceYProperty();
        DoubleProperty targetXProperty();
        DoubleProperty targetYProperty();
        Node impl_getPeer();
        ActiveLinkHelper getActiveLink();
    }
}
