package thito.nodeflow.api.node.state;

import javafx.beans.property.*;
import thito.nodeflow.api.config.*;
import thito.nodeflow.api.editor.node.*;

import java.util.*;

public interface ComponentState extends State {
    String getName();
    void setName(String name);
    String getProviderID();
    void setProviderID(String id);
    NodeProvider getProvider();
    ComponentParameterState[] getParameters();
    UUID getID();
    double getX();
    double getY();
    void setX(double x);
    void setY(double y);
    Section getExtras();
    ObjectProperty<NodeTag> getTag();
}
