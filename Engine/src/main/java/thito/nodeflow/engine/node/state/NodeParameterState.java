package thito.nodeflow.engine.node.state;

import java.io.*;
import java.util.*;

public class NodeParameterState implements State {
    @Serial
    private static final long serialVersionUID = 1L;
    public UUID id;
    public HandlerState handlerState;
}
