package thito.nodeflow.internal.task;

import thito.nodeflow.api.task.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

public class CompletableFutureSupplierImpl<T> implements CompletableFutureSupplier<T> {
    private T value;
    private Throwable error;
    private Set<Consumer<T>> listeners = ConcurrentHashMap.newKeySet();
    private Set<Consumer<Throwable>> errorListeners = ConcurrentHashMap.newKeySet();
    private boolean complete;

    @Override
    public void reset() {
        complete = false;
        value = null;
    }

    @Override
    public boolean isComplete() {
        return complete;
    }

    @Override
    public void complete(T value) {
        complete = true;
        this.value = value;
        this.error = null;
        for (Consumer<T> listener : listeners) {
            listener.accept(value);
        }
        listeners.clear();
    }

    @Override
    public void error(Throwable error) {
        this.value = null;
        this.error = error;
        for (Consumer<Throwable> errorListener : errorListeners) {
            errorListener.accept(error);
        }
        errorListeners.clear();
    }

    @Override
    public CompletableFutureSupplier<T> andThenError(Consumer<Throwable> error) {
        if (complete && this.error != null) {
            error.accept(this.error);
        } else {
            errorListeners.add(error);
        }
        return this;
    }

    @Override
    public CompletableFutureSupplier<T> andThen(Consumer<T> consumer) {
        if (!complete) {
            listeners.add(consumer);
        } else {
            consumer.accept(value);
        }
        return this;
    }

}
