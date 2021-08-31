package thito.nodeflow.engine.node.state;

import java.io.*;
import java.util.*;

public class NodeCanvasState implements State {
    @Serial
    private static final long serialVersionUID = 1L;
    public double offsetX;
    public double offsetY;
    public double scale;
    public List<NodeState> nodeStateList = new ArrayList<>();
    public List<NodeLinkedState> nodeLinkedStateList = new ArrayList<>();
    public List<NodeGroupState> groupStateList = new ArrayList<>();
    public NodeState eventNodeState;
}
