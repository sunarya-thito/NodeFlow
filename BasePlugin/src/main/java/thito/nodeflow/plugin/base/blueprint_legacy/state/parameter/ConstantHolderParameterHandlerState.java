package thito.nodeflow.plugin.base.blueprint_legacy.state.parameter;

import thito.nodeflow.engine.node.state.*;

import java.io.*;

public class ConstantHolderParameterHandlerState implements HandlerState {
    @Serial
    private static final long serialVersionUID = 1L;
    public Object value;
}
