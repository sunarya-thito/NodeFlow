package thito.nodeflow.engine;

import javafx.beans.property.*;
import javafx.collections.*;
import javafx.scene.Node;
import javafx.scene.paint.*;
import thito.nodeflow.engine.style.*;
import thito.nodeflow.engine.util.*;

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
