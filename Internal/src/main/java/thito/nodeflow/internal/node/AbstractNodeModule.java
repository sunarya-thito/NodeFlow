package thito.nodeflow.internal.node;

import javafx.collections.*;
import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.node.*;

import java.util.*;

public abstract class AbstractNodeModule implements NodeModule {
    public abstract ObservableSet<Node> nodes();

    public abstract ObservableSet<Link> links();

    public abstract ObservableSet<NodeGroup> groups();

    @Override
    public Set<Node> getNodes() {
        return Collections.unmodifiableSet(nodes());
    }

    @Override
    public Set<Link> getLinks() {
        return Collections.unmodifiableSet(links());
    }

    @Override
    public Set<NodeGroup> getGroups() {
        return Collections.unmodifiableSet(groups());
    }
}
