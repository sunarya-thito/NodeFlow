package thito.nodeflow.engine.node.util;

import javafx.beans.*;
import javafx.beans.value.*;

import java.lang.ref.*;

public class WeakReferencedChangeListener<T> implements ChangeListener<T>, WeakListener {
    private final WeakReference reference;
    private final ChangeListener<T> listener;

    public WeakReferencedChangeListener(Object reference, ChangeListener<T> listener) {
        this.reference = new WeakReference(reference);
        this.listener = listener;
    }

    @Override
    public boolean wasGarbageCollected() {
        return reference.get() == null;
    }

    @Override
    public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
        listener.changed(observable, oldValue, newValue);
    }
}
