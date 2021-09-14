package thito.nodeflow.engine.node;

import java.util.function.*;

public class InsertFunction {
    private Function<NodeCanvas, NodeParameter> function;

    public InsertFunction(Function<NodeCanvas, NodeParameter> function) {
        this.function = function;
    }

    public Function<NodeCanvas, NodeParameter> getFunction() {
        return function;
    }
}
