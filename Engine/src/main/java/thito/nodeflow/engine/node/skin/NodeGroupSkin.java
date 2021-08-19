package thito.nodeflow.engine.node.skin;

import javafx.scene.layout.*;
import thito.nodeflow.engine.node.*;

public class NodeGroupSkin extends Skin {
    private final NodeGroup group;
    private final BackdropSkin backdropSkin;

    private final Corner
    top = new Corner();
    private final Corner topRight = new Corner();
    private final Corner right = new Corner();
    private final Corner bottomRight = new Corner();
    private final Corner bottom = new Corner();
    private final Corner bottomLeft = new Corner();
    private final Corner left = new Corner();
    private final Corner topLeft = new Corner()
    ;

    public NodeGroupSkin(NodeGroup group) {
        this.group = group;
        backdropSkin = new BackdropSkin();
        getChildren().addAll(top, topRight, right, bottomRight, bottom, bottomRight, bottomLeft, left, topLeft);
    }

    public BackdropSkin getBackdropSkin() {
        return backdropSkin;
    }

    public class Corner extends Pane {
        public Corner() {
            setWidth(20);
            setHeight(20);
        }
    }

    public class BackdropSkin extends Skin {

    }
}
