package thito.nodeflow.api.task;

import java.util.List;
import java.util.function.Supplier;

public interface TaskManager {
    String BACKGROUND_THREAD = "background_thread";
    String FOREGROUND_THREAD = "foreground_thread";
    String DEBUGGER_THREAD = "debugger_thread";

    GroupTask groupTask(String name, List<? extends Task> tasks);

    WeakTask createWeakTask(Task task);

    DynamicTask createDynamicTask(Task task);

    Task createTask(String name, Runnable runnable);

    TaskThread getThread(String name);

    <T> FutureSupplier<T> createFutureSupplier(TaskThread thread, Supplier<T> supplier);

    <T> CompletableFutureSupplier createCompletableFutureSupplier();

    Future createFuture(TaskThread thread, Runnable runnable);

    CompletableFuture createCompletableFuture();

    Duration duration(long millis);

    default TaskThread getBackgroundThread() {
        return getThread(BACKGROUND_THREAD);
    }

    default TaskThread getForegroundThread() {
        return getThread(FOREGROUND_THREAD);
    }
}
