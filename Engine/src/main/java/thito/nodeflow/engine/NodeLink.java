package thito.nodeflow.engine;

import javafx.beans.property.*;

public interface NodeLink {
    NodeCanvas getCanvas();
    NodeParameter getSource();
    NodeParameter getTarget();
    ObjectProperty<LinkStyle.Handler> styleHandlerProperty();
}
