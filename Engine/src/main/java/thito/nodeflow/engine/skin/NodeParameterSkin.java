package thito.nodeflow.engine.skin;

import thito.nodeflow.engine.*;

public class NodeParameterSkin extends Skin {
    private NodeParameter parameter;

    public NodeParameterSkin(NodeParameter parameter) {
        this.parameter = parameter;
    }

    public NodeParameter getParameter() {
        return parameter;
    }
}
