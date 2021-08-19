package thito.nodeflow.engine.node;

import java.util.function.*;

public class InsertFunction {
    private Function<NodeCanvas, NodeParameter> function;

    public Function<NodeCanvas, NodeParameter> getFunction() {
        return function;
    }
}
