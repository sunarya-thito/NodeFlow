package thito.nodeflow.engine.state;

import java.util.*;

public class NodeCanvasState implements State {
    public double offsetX;
    public double offsetY;
    public double scale;
    public List<NodeState> nodeStateList = new ArrayList<>();
    public List<NodeLinkedState> nodeLinkedStateList = new ArrayList<>();
    public List<NodeGroupState> groupStateList = new ArrayList<>();
}
