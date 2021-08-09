package thito.nodeflow.internal;

import javafx.collections.*;
import thito.nodeflow.api.editor.*;
import thito.nodeflow.api.locale.*;

import java.util.*;

public class UndoableListListener<T> implements ListChangeListener<T> {
    private UndoManager manager;
    private I18nItem message;
    private I18nItem message2;

    public UndoableListListener(UndoManager manager, I18nItem messageAdd, I18nItem messageRemove) {
        this.manager = manager;
        this.message = messageAdd;
        this.message2 = messageRemove;
    }

    @Override
    public void onChanged(Change<? extends T> c) {
        while (c.next()) {
            if (c.wasAdded()) {
                List<? extends T> change = new ArrayList<>(c.getAddedSubList());
                if (change.size() > 0) {
                    manager.storeAction(message.stringBinding(), () -> {
                        c.getList().removeAll(change);
                    }, () -> {
                        c.getList().addAll((List) change);
                    });
                }
            }
            if (c.wasRemoved()) {
                List<? extends T> change = new ArrayList<>(c.getRemoved());
                if (change.size() > 0) {
                    manager.storeAction(message2.stringBinding(), () -> {
                        c.getList().addAll((List) change);
                    }, () -> {
                        c.getList().removeAll(change);
                    });
                }
            }
        }
    }
}
