package thito.nodeflow.internal.ui.editor;

import javafx.beans.property.*;
import javafx.scene.control.*;
import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.node.*;
import thito.nodeflow.library.ui.layout.*;

public class NodeComponentUI extends UIComponent {

    @Component("node-component-name")
    private ObjectProperty<Label> name = new SimpleObjectProperty<>();
    @Component("node-component-id")
    private ObjectProperty<Label> id = new SimpleObjectProperty<>();

    private NodeProvider provider;
    private NodeModule module;
    public NodeComponentUI(NodeProvider provider, NodeModule module) {
        this.provider = provider;
        this.module = module;
        setLayout(Layout.loadLayout("ComponentUI"));
    }

    @Override
    protected void onLayoutReady() {
        name.get().setText(provider.getName());
        id.get().setText(provider.getCategory().getName()+" ("+provider.getDescription()+")");
    }
}
