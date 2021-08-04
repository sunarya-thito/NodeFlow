package thito.nodeflow.internal.ui.editor;

import javafx.beans.property.*;
import javafx.scene.control.*;
import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.library.ui.layout.*;

public class NodeCategoryUI extends UIComponent {

    @Component("node-category-alias")
    private ObjectProperty<Label> alias = new SimpleObjectProperty<>();
    @Component("node-category-name")
    private ObjectProperty<Label> name = new SimpleObjectProperty<>();
    private NodeProviderCategory category;
    public NodeCategoryUI(NodeProviderCategory category) {
        this.category = category;
        setLayout(Layout.loadLayout("ComponentCategoryUI"));
    }

    @Override
    protected void onLayoutReady() {
        name.get().setText(category.getName());
        alias.get().setText(category.getAlias());
    }
}
