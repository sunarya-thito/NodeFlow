package thito.nodeflow.engine.skin;

import javafx.scene.layout.*;
import thito.nodeflow.engine.*;

public class NodeGroupSkin extends Skin {
    private NodeGroup group;
    private BackdropSkin backdropSkin;

    private Corner
    top = new Corner(),
    topRight = new Corner(),
    right = new Corner(),
    bottomRight = new Corner(),
    bottom = new Corner(),
    bottomLeft = new Corner(),
    left = new Corner(),
    topLeft = new Corner()
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
