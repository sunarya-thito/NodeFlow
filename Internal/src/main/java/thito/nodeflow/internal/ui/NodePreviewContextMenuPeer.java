package thito.nodeflow.internal.ui;

import javafx.beans.property.*;
import javafx.scene.layout.*;
import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.node.*;
import thito.nodeflow.internal.node.*;
import thito.nodeflow.library.ui.layout.*;
import thito.nodejfx.Node;

public class NodePreviewContextMenuPeer extends UIComponent {

    @Component("viewport")
    private ObjectProperty<FlowPane> viewport = new SimpleObjectProperty<>();
    private NodeModule module;
    private NodeProvider component;
    public NodePreviewContextMenuPeer(NodePreviewContextMenu menu, NodeModule module, NodeProvider component) {
        this.module = module;
        this.component = component;
        setLayout(Layout.loadLayout("NodePreviewContextMenuUI"));
    }

    @Override
    protected void onLayoutReady() {
        NodeImpl comp = (NodeImpl) component.createComponent(module);
        Node node = comp.impl_getPeer();
        node.highlightProperty().set(true);
        viewport.get().getChildren().setAll(node);
    }

}
