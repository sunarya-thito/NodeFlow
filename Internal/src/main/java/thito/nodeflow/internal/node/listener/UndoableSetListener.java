package thito.nodeflow.internal.node.listener;

import javafx.collections.*;
import thito.nodeflow.api.editor.*;
import thito.nodeflow.api.locale.*;

public class UndoableSetListener implements SetChangeListener {
    protected I18nItem name, removeName;
    protected boolean ignore;
    protected UndoManager manager;

    public UndoableSetListener(UndoManager manager, I18nItem name, I18nItem removeName) {
        this.name = name;
        this.removeName = removeName;
        this.manager = manager;
    }

    @Override
    public void onChanged(Change change) {
        if (ignore) return;
        if (change.wasAdded()) {
            Object added = change.getElementAdded();
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
