package thito.nodeflow.internal.node;

import javafx.collections.*;
import thito.nodeflow.api.config.*;
import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.node.*;
import thito.nodeflow.api.project.*;
import thito.nodeflow.api.resource.*;
import thito.nodeflow.internal.editor.*;

import java.io.*;
import java.util.*;

public class StandardNodeModule extends AbstractNodeModule implements NodeModule {
    private long id = System.currentTimeMillis();
    private final String name;
    private final ObservableSet<Node> nodes = FXCollections.observableSet();
    private final ObservableSet<Link> links = FXCollections.observableSet();
    private final ObservableSet<NodeGroup> nodeGroups = FXCollections.observableSet();
    private ModuleEditorState editorState = new ModuleEditorStateImpl();
    private NodeFileSession session;
    private Project project;
    private ResourceFile file;

    public StandardNodeModule(String name, ResourceFile file) {
        this.name = name;
        this.file = file;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Project getProject() {
        return project;
    }

    @Override
    public ResourceFile getFile() {
        return file;
    }

    public void attemptSave() {
        if (session != null) {
            session.save();
        }
    }

    public void setSession(NodeFileSession session) {
        this.session = session;
    }

    public NodeFileSession getSession() {
        return session;
    }

    @Override
    public ModuleEditorState getEditorState() {
        return editorState;
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
    public Set<Node> getNodes() {
        return Collections.unmodifiableSet(nodes);
    }

    @Override
    public Set<Link> getLinks() {
        return Collections.unmodifiableSet(links);
    }

    @Override
    public Set<NodeGroup> getGroups() {
        return Collections.unmodifiableSet(nodeGroups);
    }

    public ObservableSet<Node> nodes() {
        return nodes;
    }

    public ObservableSet<Link> links() {
        return links;
    }

    public ObservableSet<NodeGroup> groups() {
        return nodeGroups;
    }

    public synchronized void saveAs(OutputStream outputStream) throws IOException {
        ModuleManagerImpl.getInstance().saveModule(this, outputStream);
    }

    public void loadFrom(InputStream inputStream, boolean ignoreMissingProvider) throws MissingProviderException {
        ModuleManagerImpl.getInstance().loadModule(this, inputStream, ignoreMissingProvider);
    }
}
