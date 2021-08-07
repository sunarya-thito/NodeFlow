package thito.nodeflow.engine.skin;

import javafx.scene.layout.*;
import thito.nodeflow.engine.*;

public class NodeSkin extends Skin {
    private Node node;

    public NodeSkin(Node node) {
        this.node = node;
    }

    public Node getNode() {
        return node;
    }
}
