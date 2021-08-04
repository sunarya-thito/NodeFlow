package thito.nodeflow.internal.node.headless;

import javafx.collections.*;
import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.node.*;
import thito.nodeflow.api.resource.*;
import thito.nodeflow.internal.node.*;

public class HeadlessNodeModule extends AbstractNodeModule implements NodeModule {
    private ObservableSet<Node> nodes = FXCollections.observableSet();
    private ObservableSet<Link> links = FXCollections.observableSet();
    private ObservableSet<NodeGroup> groups = FXCollections.observableSet();
    private long id;
    private String name;
    private ModuleEditorState state = new ModuleEditorStateImpl();
    private ResourceFile file;

    public HeadlessNodeModule(long id, String name, ResourceFile file) {
        this.id = id;
        this.name = name;
        this.file = file;
    }

    public ResourceFile getFile() {
        return file;
    }

    @Override
    public ObservableSet<Node> nodes() {
        return nodes;
    }

    @Override
    public ObservableSet<Link> links() {
        return links;
    }

    @Override
    public ObservableSet<NodeGroup> groups() {
        return groups;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ModuleEditorState getEditorState() {
        return state;
    }
}
