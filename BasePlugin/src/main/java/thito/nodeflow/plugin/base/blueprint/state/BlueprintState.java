package thito.nodeflow.plugin.base.blueprint.state;

import thito.nodeflow.engine.node.state.NodeCanvasState;

import java.io.Serial;
import java.io.Serializable;

public class BlueprintState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    public double offsetX, offsetY;
    public VariableState[] variables;
    public NodeCanvasState[] procedures;
}
