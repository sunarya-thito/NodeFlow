package thito.nodeflow.internal.node;

import thito.nodeflow.api.editor.*;
import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.node.*;
import thito.nodeflow.api.node.state.*;
import thito.nodeflow.internal.node.state.*;

public class NodeGroupImpl implements NodeGroup {
    private GroupState state;
    private NodeModule module;

    public NodeGroupImpl(NodeModule module, GroupState state) {
        this.module = module;
        this.state = state;
        ((GroupStateImpl) state).setModule((StandardNodeModule) module);
    }

    @Override
    public void remove() {
        ((AbstractNodeModule) module).groups().remove(this);
    }

    private thito.nodejfx.NodeGroup nodeGroup;

    public void setGroup(thito.nodejfx.NodeGroup nodeGroup) {
        this.nodeGroup = nodeGroup;
        nodeGroup.layoutXProperty().addListener((obs, old, val) -> {
            state.setX(val.doubleValue());
            ((GroupStateImpl) state).getModule().attemptSave();
        });
        nodeGroup.layoutYProperty().addListener((obs, old, val) -> {
            state.setY(val.doubleValue());
            ((GroupStateImpl) state).getModule().attemptSave();
        });
        nodeGroup.getTopPos().addListener((obs, old, val) -> {
            state.setMinY(val.doubleValue());
            ((GroupStateImpl) state).getModule().attemptSave();
        });
        nodeGroup.getRightPos().addListener((obs, old, val) -> {
            state.setMaxX(val.doubleValue());
            ((GroupStateImpl) state).getModule().attemptSave();
        });
        nodeGroup.getBottomPos().addListener((obs, old, val) -> {
            state.setMaxY(val.doubleValue());
            ((GroupStateImpl) state).getModule().attemptSave();
        });
        nodeGroup.getLeftPos().addListener((obs, old, val) -> {
            state.setMinX(val.doubleValue());
            ((GroupStateImpl) state).getModule().attemptSave();
        });
        nodeGroup.getGroupName().addListener((obs, old, val) -> {
            state.setName(val);
            ((GroupStateImpl) state).getModule().attemptSave();
        });
        nodeGroup.dropPointProperty().addListener((obs, old, val) -> {
            if (old != null) {
                EditorAction.store(((GroupStateImpl) state).getModule().getSession(), I18n.$("action-move-group"), () -> {
                    nodeGroup.setLayoutX(old.getX());
                    nodeGroup.setLayoutY(old.getY());
                }, () -> {
                    if (val != null) {
                        nodeGroup.setLayoutX(val.getX());
                        nodeGroup.setLayoutY(val.getY());
                    }
                });
            }
        });
        nodeGroup.resizeBoundsProperty().addListener((obs, old, val) -> {
            if (old != null) {
                EditorAction.store(((GroupStateImpl) state).getModule().getSession(), I18n.$("action-move-group"), () -> {
                    nodeGroup.getTopPos().set(old.getMinY());
                    nodeGroup.getLeftPos().set(old.getMinX());
                    nodeGroup.getBottomPos().set(old.getMaxY());
                    nodeGroup.getRightPos().set(old.getMaxX());
                }, () -> {
                    if (val != null) {
                        nodeGroup.getTopPos().set(val.getMinY());
                        nodeGroup.getLeftPos().set(val.getMinX());
                        nodeGroup.getBottomPos().set(val.getMaxY());
                        nodeGroup.getRightPos().set(val.getMaxX());
                    }
                });
            }
        });
    }

    public thito.nodejfx.NodeGroup impl_getPeer() {
        if (nodeGroup == null) {
            nodeGroup = new thito.nodejfx.NodeGroup();
            nodeGroup.getGroupName().set(state.getName());
            nodeGroup.setUserData(this);
            nodeGroup.setLayoutX(state.getX());
            nodeGroup.setLayoutY(state.getY());
            nodeGroup.getTopPos().set(state.getMinY());
            nodeGroup.getBottomPos().set(state.getMaxY());
            nodeGroup.getLeftPos().set(state.getMinX());
            nodeGroup.getRightPos().set(state.getMaxX());
            setGroup(nodeGroup);
        }
        return nodeGroup;
    }

    @Override
    public GroupState getState() {
        return state;
    }
}
