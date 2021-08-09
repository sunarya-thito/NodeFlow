package thito.nodeflow.internal.node.listener;

import javafx.beans.*;
import javafx.scene.paint.*;
import thito.nodeflow.api.editor.*;
import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.internal.editor.*;
import thito.nodeflow.internal.node.*;
import thito.nodejfx.parameter.*;

import java.util.*;

public class NodeSetListener extends UndoableSetListener {
    private NodeFileSession session;
    public NodeSetListener(NodeFileSession session, UndoManager undoManager, I18nItem name, I18nItem removeName) {
        super(undoManager, name, removeName);
        this.session = session;
    }

    @Override
    public void onChanged(Change change) {
        if (ignore) return;
        if (change.wasAdded()) {
            Object added = change.getElementAdded();
            for (thito.nodejfx.NodeParameter param : ((NodeImpl) added).impl_getPeer().getParameters()) {
                if (param instanceof SpecificParameter && param.getAllowInput().get()) {
                    ((UserInputParameter<?>) param).valueProperty().addListener(session::saveObs);
                    InvalidationListener check = obs -> {
                        if (param.getUnmodifiableInputLinks().size() > 0) {
                            ((UserInputParameter<?>) param).getLabel().textFillProperty().set(Color.WHITE);
                        } else {
                            ((UserInputParameter<?>) param).getLabel().textFillProperty().set(Color.GOLD);
                        }
                    };
                    param.getUnmodifiableInputLinks().addListener(check);
                    check.invalidated(null);
                }
            }
            manager.storeAction(name.stringBinding(), () -> {
                synchronized (this) {
                    ignore = true;
                    change.getSet().remove(added);
                    ignore = false;
                }
            }, () -> {
                synchronized (this) {
                    ignore = true;
                    change.getSet().add(added);
                    ignore = false;
                }
            });
        }
        if (change.wasRemoved()) {
            Object removed = change.getElementRemoved();
            List<Link> removal = new ArrayList<>();
            for (Link link : session.getModule().links()) {
                if (link.getSource().getNode() == removed || link.getTarget().getNode() == removed) {
                    removal.add(link);
                }
            }
            for (Link link : removal) session.getModule().links().remove(link);
            manager.storeAction(removeName.stringBinding(), () -> {
                synchronized (this) {
                    ignore = true;
                    change.getSet().add(removed);
                    ignore = false;
                }
            }, () -> {
                synchronized (this) {
                    ignore = true;
                    change.getSet().remove(removed);
                    ignore = false;
                }
            });
        }
    }
}
