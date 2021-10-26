package thito.nodeflow.plugin.base.blueprint_legacy.compiler;

import thito.nodeflow.engine.node.Node;
import thito.nodeflow.engine.node.NodeParameter;
import thito.nodeflow.engine.node.handler.NodeParameterHandler;

public class CompileHelper {
    public static <T extends NodeParameterHandler> T getParameterHandler(Node node, Class<T> clazz) {
        for (NodeParameter parameter : node.getParameters()) {
            if (clazz.isInstance(parameter.getHandler())) {
                return (T) parameter.getHandler();
            }
        }
        return null;
    }
}
