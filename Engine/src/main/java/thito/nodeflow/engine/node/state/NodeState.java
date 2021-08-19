package thito.nodeflow.engine.node.state;

import java.util.*;

public class NodeState implements State {
    public UUID id;
    public double x;
    public double y;
    public HandlerState handlerState;
    public List<NodeParameterState> nodeParameterStateList = new ArrayList<>();
}
