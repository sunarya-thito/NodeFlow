package thito.nodeflow.library.binding;

import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.collections.*;
import javafx.scene.*;

import java.lang.ref.*;
import java.util.function.*;

public class SelectableGroup<T extends Node> {

    private ObservableList<T> list;
    private Function<T, BooleanProperty> getter;
    private ObjectProperty<T> selected = new SimpleObjectProperty<>();

    public SelectableGroup(ObservableList<T> list, Function<T, BooleanProperty> getter) {
        this.list = list;
        this.getter = getter;
        list.addListener((ListChangeListener<T>) c -> {
            while (c.next()) {
                for (T added : c.getAddedSubList()) {
                    hookUp(added);
                }
                for (T removed : c.getRemoved()) {
                    hookDown(removed);
                }
            }
        });
        for (T alreadyAdded : list) {
            hookUp(alreadyAdded);
        }
    }

    public T getSelected() {
        return selected.get();
    }

    public void setSelected(T selected) {
        if (selected == null) {
            selected = this.selected.get();
            if (selected != null) {
                BooleanProperty hooked = getter.apply(selected);
                hooked.set(false);
            }
            return;
        }
        BooleanProperty hooked = getter.apply(selected);
        hooked.set(true);
    }

    public ReadOnlyObjectProperty<T> selectedProperty() {
        return selected;
    }

    public void stop() {
        for (T node : list) {
            hookDown(node);
        }
    }

    private void hookUp(T node) {
        BooleanProperty hooked = getter.apply(node);
        ChangeListener<Boolean> changeListener;
        hooked.addListener(changeListener = new WeakReferencedChangeListener<Boolean>(new WeakReference<>(this)) {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    selected.set(node);
                    for (T other : list) {
                        if (other != node) {
                            BooleanProperty otherProperty = getter.apply(other);
                            otherProperty.set(false);
                        }
                    }
                }
            }
        });
        node.getProperties().put(this, changeListener);
    }

    private void hookDown(T node) {
        Object object = node.getProperties().remove(this);
        if (object instanceof ChangeListener) {
            BooleanProperty hooked = getter.apply(node);
            hooked.removeListener((ChangeListener<? super Boolean>) object);
        }
    }

}
