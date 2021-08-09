package thito.nodeflow.api.task;

import thito.nodeflow.api.NodeFlow;

import java.util.function.*;

public interface Future {
    static CompletableFuture createCompletableFuture() {
        return NodeFlow.getApplication().getTaskManager().createCompletableFuture();
    }

    static Future createFuture(TaskThread taskThread, Runnable runnable) {
        return NodeFlow.getApplication().getTaskManager().createFuture(taskThread, runnable);
    }

    static Future createBackgroundFuture(Runnable runnable) {
        return createFuture(NodeFlow.getApplication().getTaskManager().getBackgroundThread(), runnable);
    }

    static Future createForegroundFuture(Runnable runnable) {
        return createFuture(NodeFlow.getApplication().getTaskManager().getForegroundThread(), runnable);
    }

    default void executeNow() {
        throw new UnsupportedOperationException();
    }

    Future andThen(Consumer<Throwable> acceptError);

    Future andThen(Runnable after);
}
