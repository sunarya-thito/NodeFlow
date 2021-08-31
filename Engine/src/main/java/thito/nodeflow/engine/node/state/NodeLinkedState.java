package thito.nodeflow.engine.node.state;

import java.io.*;
import java.util.*;

public class NodeLinkedState implements State {
    @Serial
    private static final long serialVersionUID = 1L;
    public UUID sourceId, targetId;
}
