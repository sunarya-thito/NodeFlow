package thito.nodeflow.binding;

import javafx.beans.*;

import java.lang.ref.*;

public class WeakReferencedListener implements InvalidationListener, WeakListener {
    private InvalidationListener wrapped;
    private WeakReference<?> targetReference;

    public WeakReferencedListener(InvalidationListener wrapped, Object targetReference) {
        this.wrapped = wrapped;
        this.targetReference = new WeakReference<>(targetReference);
    }

    @Override
    public void invalidated(Observable observable) {
        wrapped.invalidated(observable);
    }

    @Override
    public boolean wasGarbageCollected() {
        return targetReference.get() == null;
    }
}
