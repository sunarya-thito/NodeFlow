package thito.nodeflow.internal.binding;

import javafx.beans.*;
import javafx.collections.*;

import java.lang.ref.*;
import java.util.*;

public class CombinedListBinding<T> implements ListChangeListener<T>, WeakListener {

    public static <T> CombinedListBinding<T> combine(List<T> result, ObservableList<? extends T> listA, ObservableList<? extends T> listB) {
        result.clear();
        result.addAll(listA);
        result.addAll(listB);
        CombinedListBinding<T> combinedListBinding = new CombinedListBinding<>(result, listA, listB);
        combinedListBinding.unbind();
        listA.addListener(combinedListBinding);
        listB.addListener(combinedListBinding);
        return combinedListBinding;
    }

    private WeakReference<List<T>> target;
    private ObservableList<? extends T> listA, listB;

    public CombinedListBinding(List<T> target, ObservableList<? extends T> listA, ObservableList<? extends T> listB) {
        this.target = new WeakReference<>(target);
        this.listA = listA;
        this.listB = listB;
    }

    public void unbind() {
        listA.removeListener(this);
        listB.removeListener(this);
    }

    @Override
    public boolean wasGarbageCollected() {
        return target.get() == null;
    }

    @Override
    public synchronized void onChanged(Change<? extends T> change) {
        List<T> target = this.target.get();
        if (target == null) unbind();
        else synchronized (target) {
            while (change.next()) {
                if (change.wasPermutated()) {
                    if (change.getList() == listA) {
                        target.subList(change.getFrom(), change.getTo()).clear();
                        target.addAll(change.getFrom(), change.getList().subList(change.getFrom(), change.getTo()));
                    } else {
                        target.subList(listA.size() + change.getFrom(), listA.size() + change.getTo()).clear();
                        target.addAll(listA.size() + change.getFrom(), change.getList().subList(change.getFrom(), change.getTo()));
                    }
                } else {
                    if (change.wasRemoved()) {
                        if (change.getList() == listA) {
                            target.subList(change.getFrom(), change.getFrom() + change.getRemovedSize()).clear();
                        } else {
                            target.subList(listA.size() + change.getFrom(), listA.size() + change.getFrom() + change.getRemovedSize()).clear();
                        }
                    }
                    if (change.wasAdded()) {
                        if (change.getList() == listA) {
                            target.addAll(change.getFrom(), change.getAddedSubList());
                        } else {
                            target.addAll(listA.size() + change.getFrom(), change.getAddedSubList());
                        }
                    }
                }
            }
        }
    }
}
