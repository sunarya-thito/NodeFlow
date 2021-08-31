package thito.nodeflow.engine.node.state;

import java.io.*;
import java.util.*;

public class NodeGroupState implements State {
    @Serial
    private static final long serialVersionUID = 1L;
    public UUID id;
    public double x, y, width, height;
    public String name;
}
