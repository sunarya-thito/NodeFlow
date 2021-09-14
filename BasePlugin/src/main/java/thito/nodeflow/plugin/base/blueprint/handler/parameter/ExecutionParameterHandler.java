package thito.nodeflow.plugin.base.blueprint.handler.parameter;

import javafx.scene.paint.*;
import thito.nodeflow.engine.node.*;
import thito.nodeflow.engine.node.state.*;
import thito.nodeflow.plugin.base.blueprint.state.parameter.*;

public class ExecutionParameterHandler extends AbstractExecutionParameterHandler {
    private NodePort input, output;

    public ExecutionParameterHandler(NodeParameter nodeParameter) {
        super(nodeParameter);
        this.input = new NodePort(true, Color.WHITE, PortShape.RHOMBUS);
        this.output = new NodePort(false, Color.WHITE, PortShape.RHOMBUS);
    }

    @Override
    public NodePort getInputPort() {
        return input;
    }

    @Override
    public NodePort getOutputPort() {
        return output;
    }

    @Override
    public HandlerState saveState() {
        return new ExecutionParameterHandlerState();
    }
}
