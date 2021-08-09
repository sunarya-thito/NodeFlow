package thito.nodeflow.library.binding;

import javafx.beans.*;
import javafx.collections.*;

import java.lang.ref.*;

public abstract class WeakReferencedSetChangeListener<E> implements SetChangeListener<E>, WeakListener {

    private WeakReference<?> reference;

    public WeakReferencedSetChangeListener(WeakReference<?> reference) {
        this.reference = reference;
    }

    @Override
    public boolean wasGarbageCollected() {
        return reference.get() == null;
    }

}
