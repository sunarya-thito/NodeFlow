package thito.nodeflow.internal.task;

import thito.nodeflow.api.task.CompletableFuture;
import thito.nodeflow.api.task.Future;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

public class CompletableFutureImpl implements CompletableFuture {
    private boolean complete;
    private Throwable error;
    private Set<Object> listeners = ConcurrentHashMap.newKeySet();
    @Override
    public void complete() {
        complete = true;
        for (Object r : listeners) {
            if (r instanceof Runnable) {
                ((Runnable) r).run();
            }
        }
    }

    @Override
    public boolean isComplete() {
        return complete;
    }

    @Override
    public void error(Throwable t) {
        complete = true;
        error = t;
        for (Object r : listeners) {
            if (r instanceof Consumer) {
                ((Consumer<Throwable>) r).accept(t);
            }
        }
    }

    @Override
    public void reset() {
        complete = false;
    }

    @Override
    public Future andThen(Runnable after) {
        if (complete) {
            after.run();
        } else {
            listeners.add(after);
        }
        return this;
    }

    @Override
    public Future andThen(Consumer<Throwable> acceptError) {
        if (complete) {
            if (error != null) {
                acceptError.accept(error);
            }
        } else {
            listeners.add(acceptError);
        }
        return this;
    }
}
