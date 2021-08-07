package thito.nodeflow.engine.skin;

import javafx.beans.value.*;
import javafx.scene.layout.*;
import thito.nodeflow.engine.*;

public class NodeCanvasSkin extends Skin {

    private Pane selectionLayer = new Pane();
    private Pane groupLayer = new Pane();
    private Pane nodeLayer = new Pane();
    private Pane linkLayer = new Pane();
    private Pane groupBackgroundLayer = new Pane();

    public NodeCanvasSkin() {
        getChildren().addAll(selectionLayer, groupLayer, nodeLayer, linkLayer, groupBackgroundLayer);
        for (int i = 0; i < getChildren().size(); i++) {
            Pane child = (Pane) getChildren().get(i);
            child.minWidthProperty().bind(widthProperty());
            child.minHeightProperty().bind(heightProperty());
            child.maxWidthProperty().bind(widthProperty());
            child.maxHeightProperty().bind(heightProperty());
        }
    }

    public void onNodeAdded(Node node) {
        nodeLayer.getChildren().add(node.getSkin());
    }

    public void onNodeRemoved(Node node) {
        nodeLayer.getChildren().remove(node.getSkin());
    }

    public void onGroupAdded(NodeGroup group) {
        groupLayer.getChildren().add(group.getSkin());
        groupBackgroundLayer.getChildren().add(group.getSkin().getBackdropSkin());
    }

    public void onGroupRemoved(NodeGroup group) {
        groupLayer.getChildren().remove(group.getSkin());
        groupBackgroundLayer.getChildren().remove(group.getSkin().getBackdropSkin());
    }

    private DynamicNodeLink nodeLinkListener = new DynamicNodeLink(this);

    public void onLinkAdded(NodeLink link) {
        nodeLinkListener.changed(link.styleHandlerProperty(), null, link.styleHandlerProperty().get());
        link.styleHandlerProperty().addListener(nodeLinkListener);
    }

    public void onLinkRemoved(NodeLink link) {
        link.styleHandlerProperty().removeListener(nodeLinkListener);
        nodeLinkListener.changed(link.styleHandlerProperty(), link.styleHandlerProperty().get(), null);
    }

    public static class DynamicNodeLink implements ChangeListener<NodeLinkStyle.Handler> {

        private NodeCanvasSkin skin;

        public DynamicNodeLink(NodeCanvasSkin skin) {
            this.skin = skin;
        }

        @Override
        public void changed(ObservableValue<? extends NodeLinkStyle.Handler> observable, NodeLinkStyle.Handler oldValue, NodeLinkStyle.Handler newValue) {
            if (oldValue != null) {
                skin.linkLayer.getChildren().remove(oldValue.getPeer());
            }
            if (newValue != null) {
                skin.linkLayer.getChildren().add(newValue.getPeer());
            }
        }
    }

}
