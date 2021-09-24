package thito.nodeflow.internal.binding;

import javafx.beans.*;
import javafx.collections.*;

import java.lang.ref.*;
import java.util.*;

public class OffsetListBinding<T> implements ListChangeListener<T>, WeakListener {
    private WeakReference<List<T>> target;
    private int offset;
    private ObservableList<T> list;

    public static <T> OffsetListBinding<T> bind(List<T> target, ObservableList<T> source, int offset) {
        OffsetListBinding<T> binding = new OffsetListBinding<>(new WeakReference<>(target), offset, source);
        target.addAll(offset, source);
        return binding;
    }

    public OffsetListBinding(WeakReference<List<T>> target, int offset, ObservableList<T> list) {
        this.target = target;
        this.offset = offset;
        this.list = list;
    }

    @Override
    public boolean wasGarbageCollected() {
        return target.get() == null;
    }

    public void unbind() {
        list.removeListener(this);
    }

    @Override
    public void onChanged(Change<? extends T> change) {
        List<T> target = this.target.get();
        if (target == null) {
            unbind();
        } else {
            while (change.next()) {
                if (change.wasPermutated()) {
                    target.subList(offset + change.getFrom(), offset + change.getTo()).clear();
                    target.addAll(offset + change.getFrom(), change.getList().subList(change.getFrom(), change.getTo()));
                } else {
                    if (change.wasRemoved()) {
                        target.subList(offset + change.getFrom(), offset + change.getFrom() + change.getRemovedSize()).clear();
                    }
                    if (change.wasAdded()) {
                        target.addAll(offset + change.getFrom(), change.getAddedSubList());
                    }
                }
            }
        }
    }
}
