package thito.nodeflow.library.binding;

import javafx.beans.*;
import javafx.beans.value.*;

import java.lang.ref.*;

public abstract class WeakReferencedChangeListener<T> implements ChangeListener<T>, WeakListener {
    private WeakReference<?> anyReference;

    public WeakReferencedChangeListener(WeakReference<?> any) {
        anyReference = any;
    }

    @Override
    public boolean wasGarbageCollected() {
        return anyReference.get() == null;
    }
}
