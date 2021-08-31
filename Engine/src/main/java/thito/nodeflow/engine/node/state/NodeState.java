package thito.nodeflow.engine.node.state;

import java.io.*;
import java.util.*;

public class NodeState implements State {
    @Serial
    private static final long serialVersionUID = 1L;
    public UUID id;
    public double x;
    public double y;
    public HandlerState handlerState;
    public List<NodeParameterState> nodeParameterStateList = new ArrayList<>();
}
