package thito.nodeflow.internal.clipboard;

import javafx.beans.binding.*;
import javafx.geometry.*;
import javafx.scene.input.*;
import thito.nodeflow.api.editor.node.Node;
import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.node.*;
import thito.nodeflow.api.node.state.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.internal.node.*;
import thito.nodeflow.internal.node.state.*;
import thito.nodeflow.library.ui.*;

import java.io.*;
import java.nio.*;
import java.util.*;
import java.util.stream.*;

public class ModuleMemberClipboard {
    public static final DataFormat FORMAT = new DataFormat("NodeFlow:MODULE_MEMBER");
    public static final BooleanBinding HAS_DATA = Bindings.createBooleanBinding(() -> {
        Ticker.TIME.get();
        return Clipboard.getSystemClipboard().hasContent(FORMAT);
    }, Ticker.TIME);

    public static final BooleanBinding HAS_FILES = Bindings.createBooleanBinding(() -> {
        Ticker.TIME.get();
        return Clipboard.getSystemClipboard().hasFiles();
    }, Ticker.TIME);

    public static void putIntoClipboard(Set<ModuleMember> members) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        Map<DataFormat, Object> content = new HashMap<>();
        Set<State> states = members.stream().map(x -> x.getState()).collect(Collectors.toSet());
        content.put(FORMAT, states);
        Toolkit.info("Put "+members.size()+" module members into clipboard!");
        clipboard.setContent(content);
    }

    public static void paste(Point2D relative, Set<ModuleMember> selected, NodeModule module) {
        Set<ModuleMember> result = getFromClipboard(module);
        if (result == null || result.isEmpty()) {
            Toolkit.info("No module members on clipboard!");
            return;
        }
        selected.clear();
        selected.addAll(result);
        if (relative != null) {
            double lowestXOffset = Double.MAX_VALUE;
            double lowestYOffset = Double.MAX_VALUE;
            for (ModuleMember member : result) {
                State state = member.getState();
                if (state instanceof GroupState) {
                    lowestXOffset = Math.min(lowestXOffset, ((GroupState) state).getX());
                    lowestYOffset = Math.min(lowestYOffset, ((GroupState) state).getY());
                } else if (state instanceof ComponentState) {
                    lowestXOffset = Math.min(lowestXOffset, ((ComponentState) state).getX());
                    lowestYOffset = Math.min(lowestYOffset, ((ComponentState) state).getY());
                }
            }
            for (ModuleMember member : result) {
                if (member instanceof NodeGroupImpl) {
                    NodeGroup gx = (NodeGroup) member;
                    thito.nodejfx.NodeGroup group = ((NodeGroupImpl) member).impl_getPeer();
                    group.setLayoutX(group.getLayoutX() - lowestXOffset + relative.getX() - gx.getState().getMinX());
                    group.setLayoutY(group.getLayoutY() - lowestYOffset + relative.getY() - gx.getState().getMinY());
                } else if (member instanceof NodeImpl) {
                    thito.nodejfx.Node node = ((NodeImpl) member).impl_getPeer();
                    node.setLayoutX(node.getLayoutX() - lowestXOffset + relative.getX());
                    node.setLayoutY(node.getLayoutY() - lowestYOffset + relative.getY());
                    ComponentStateImpl state = (ComponentStateImpl) member.getState();
                    state.randomizeID();
                }
                Toolkit.info("Pasted: "+member);
            }
        } else {
            Toolkit.info("No relative position to paste!");
        }
    }

    public static Set<ModuleMember> getFromClipboard(NodeModule module) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        if (!clipboard.hasContent(FORMAT)) {
            Toolkit.info("Clipboard doesn't have "+ FORMAT);
            return null;
        }
        Object result = clipboard.getContent(FORMAT);
        if (result instanceof ByteBuffer) {
            ByteBuffer buffer = (ByteBuffer) result;
            try (ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(buffer.array()))) {
                result = inputStream.readObject();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        if (!(result instanceof Set)) {
            Toolkit.info("Expected clipboard set but found "+result);
            return null;
        }
        Set<State> states = (Set<State>) result;
        Toolkit.info("Found raw clipboard: "+states.size()+" members");
        return states.stream().map(x -> {
            if (x instanceof ComponentState) {
                Node node = ((ComponentState) x).getProvider().fromState(module, (ComponentState) x);
                ((AbstractNodeModule) module).nodes().add(node);
                return node;
            }
            if (x instanceof GroupState) {
                NodeGroup group = new NodeGroupImpl(module, (GroupState) x);
                ((AbstractNodeModule) module).groups().add(group);
                return group;
            }
            Toolkit.info("Unknown clipboard state type: "+x);
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toSet());
    }
}
