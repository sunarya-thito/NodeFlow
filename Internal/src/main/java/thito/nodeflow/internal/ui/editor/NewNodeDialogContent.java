package thito.nodeflow.internal.ui.editor;

import javafx.beans.property.*;
import javafx.scene.layout.*;
import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.node.*;
import thito.nodeflow.api.project.*;
import thito.nodeflow.api.ui.dialog.*;
import thito.nodeflow.api.ui.dialog.content.*;

public class NewNodeDialogContent implements DialogContent {
    private ObjectProperty<NodeProvider> provider = new SimpleObjectProperty<>();
    private NodeModule module;
    private boolean lazyLoad, onlyEvents;
    private Project project;

    public NewNodeDialogContent(Project project, NodeModule module, boolean lazyLoad, boolean onlyEvents) {
        this.project = project;
        this.module = module;
        this.lazyLoad = lazyLoad;
        this.onlyEvents = onlyEvents;
    }

    @Override
    public Pane impl_createPeer(OpenedDialog dialog) {
        return new NewNodeUI(project, module,this, lazyLoad, onlyEvents);
    }

    public ObjectProperty<NodeProvider> providerProperty() {
        return provider;
    }
}
