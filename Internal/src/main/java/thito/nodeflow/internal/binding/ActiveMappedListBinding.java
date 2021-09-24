package thito.nodeflow.internal.binding;

import javafx.beans.*;
import javafx.beans.value.*;
import javafx.collections.*;

import java.lang.ref.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class ActiveMappedListBinding<F, E> implements ListChangeListener<E>, WeakListener {
    public static <F, E> ActiveMappedListBinding<ObservableValue<F>, E> bind(List<F> list1, ObservableList<? extends E> list2, Function<E, ObservableValue<F>> mapping) {
        ObservableList<ObservableValue<F>> indexer = FXCollections.observableArrayList();
        final ActiveMappedListBinding<ObservableValue<F>, E> contentBinding = new ActiveMappedListBinding<>(indexer, e -> {
            ObservableValue<F> binding = mapping.apply(e);
            binding.addListener((obs, old, val) -> {
                int index = indexer.indexOf(binding);
                if (index >= 0 && index < list1.size()) {
                    list1.set(index, val);
                }
            });
            return binding;
        }, list2);
        MappedListBinding.bind(list1, indexer, ObservableValue::getValue);
        indexer.addAll(list2.stream().map(mapping).collect(Collectors.toList()));
        list2.removeListener(contentBinding);
        list2.addListener(contentBinding);
        return contentBinding;
    }

    private final WeakReference<List<F>> listRef;
    private final Function<E, F> mapping;

    private ObservableList<? extends E> list2;

    public ActiveMappedListBinding(List<F> list, Function<E, F> mapping, ObservableList<? extends E> list2) {
        this.list2 = list2;
        this.listRef = new WeakReference<>(list);
        this.mapping = mapping;
    }

    public void unbind() {
        list2.removeListener(this);
    }

    @Override
    public synchronized void onChanged(Change<? extends E> change) {
        final List<F> list = listRef.get();
        if (list == null) {
            change.getList().removeListener(this);
        } else {
            synchronized (list) {
                while (change.next()) {
                    if (change.wasPermutated()) {
                        list.subList(change.getFrom(), change.getTo()).clear();
                        list.addAll(change.getFrom(), change.getList().subList(change.getFrom(), change.getTo()).stream().map(mapping).collect(Collectors.toList()));
                    } else {
                        if (change.wasRemoved()) {
                            list.subList(change.getFrom(), change.getFrom() + change.getRemovedSize()).clear();
                        }
                        if (change.wasAdded()) {
                            list.addAll(change.getFrom(), change.getAddedSubList().stream().map(mapping).collect(Collectors.toList()));
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean wasGarbageCollected() {
        return listRef.get() == null;
    }

    @Override
    public int hashCode() {
        final List<F> list = listRef.get();
        return (list == null)? 0 : list.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        final List<F> list1 = listRef.get();
        if (list1 == null) {
            return false;
        }

        if (obj instanceof ActiveMappedListBinding) {
            final ActiveMappedListBinding<?, ?> other = (ActiveMappedListBinding<?, ?>) obj;
            final List<?> list2 = other.listRef.get();
            return list1 == list2;
        }
        return false;
    }
}