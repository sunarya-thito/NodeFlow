package thito.nodeflow.internal.task;

import thito.nodeflow.api.task.Future;
import thito.nodeflow.api.task.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

public class FutureImpl implements Future {

    private final TaskThread thread;
    private final Runnable task;
    private boolean done;
    private Throwable error;
    private Set<Object> listeners = ConcurrentHashMap.newKeySet();

    public FutureImpl(TaskThread thread, Runnable task) {
        this.thread = thread;
        this.task = task;
        thread.runTask(new TaskImpl("Future", () -> {
            executeNow();
        }));
    }

    @Override
    public Future andThen(Runnable after) {
       if (done) {
           thread.runTask(new TaskImpl("Future", () -> {
               after.run();
           }));
       } else {
           listeners.add(after);
       }
        return this;
    }

    @Override
    public Future andThen(Consumer<Throwable> acceptError) {
        if (done) {
            thread.runTask(new TaskImpl("Future", () -> {
                if (error != null) {
                    acceptError.accept(error);
                }
            }));
        } else {
            listeners.add(acceptError);
        }
        return this;
    }

    @Override
    public void executeNow() {
        if (done) return;
        try {
            task.run();
        } catch (Throwable t) {
            error = t;
        }
        done = true;
        for (Object o : listeners) {
            if (error != null) {
                if (o instanceof Consumer) {
                    thread.runTask(new TaskImpl("Future", () -> {
                        ((Consumer<Throwable>) o).accept(error);
                    }));
                }
            } else {
                if (o instanceof Runnable) {
                    thread.runTask(new TaskImpl("Future", () -> {
                        ((Runnable) o).run();
                    }));
                }
            }
        }
    }

}
