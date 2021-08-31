package thito.nodeflow.engine.node.skin;

import thito.nodeflow.engine.node.*;

public class EventNodeSkin extends NodeSkin {
    public EventNodeSkin(EventNode node) {
        super(node);
    }

    @Override
    protected void initializePopupListener() {
        // silenced :D
    }

    @Override
    protected void initializeSkin() {
        // removed title
        getChildren().add(nodeParameterBox);
    }

}
