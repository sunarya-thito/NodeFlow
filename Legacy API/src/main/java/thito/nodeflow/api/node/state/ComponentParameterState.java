package thito.nodeflow.api.node.state;

import thito.nodeflow.api.config.*;

import java.util.*;

public interface ComponentParameterState extends State {
    UUID getID();
    Object getConstantValue();
    void setConstantValue(Object value);
    Section getExtras();
}
