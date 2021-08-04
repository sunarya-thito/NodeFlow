package thito.nodeflow.internal.node;

import thito.nodeflow.api.editor.node.NodeParameter;
import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.node.*;
import thito.nodejfx.*;

import java.util.*;

public class LinkImpl implements Link {
    private NodeModule module;
    private UUID sourceId, targetId;
    private NodeLinked peer;

    public LinkImpl(NodeModule module, UUID sourceId, UUID targetId) {
        this.module = module;
        this.sourceId = sourceId;
        this.targetId = targetId;
    }

    public LinkImpl(NodeModule module, NodeParameter source, NodeParameter target) {
        this(module, source.getID(), target.getID());
        setSource(source);
        setTarget(target);
    }

    public void impl_setPeer(NodeLinked peer) {
        this.peer = peer;
    }

    public NodeLinked impl_getPeer() {
        return peer;
    }

    public NodeModule getModule() {
        return module;
    }

    @Override
    public UUID getSourceId() {
        return sourceId;
    }

    @Override
    public UUID getTargetId() {
        return targetId;
    }

    private NodeParameter source, target;

    @Override
    public NodeParameter getSource() {
        return source;
    }

    @Override
    public NodeParameter getTarget() {
        return target;
    }

    public void setSource(NodeParameter source) {
        this.source = source;
    }

    public void setTarget(NodeParameter target) {
        this.target = target;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinkImpl link = (LinkImpl) o;
        return (sourceId.equals(link.sourceId) && targetId.equals(link.targetId)) || (sourceId.equals(link.targetId) && targetId.equals(link.sourceId));
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceId, targetId);
    }
}
