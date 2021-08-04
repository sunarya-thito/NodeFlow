package thito.nodeflow.internal.task;

import thito.nodeflow.api.*;
import thito.nodeflow.api.task.*;

import java.util.*;
import java.util.function.*;

public class TaskManagerImpl implements TaskManager {

    private final TaskThread background;
    private final TaskThread foreground;
    private final TaskThread debugger;
    private final Map<String, TaskThread> threadMap = new HashMap<>();

    public TaskManagerImpl() {
        threadMap.put(TaskManager.DEBUGGER_THREAD, debugger = new TaskThreadImpl(TaskManager.DEBUGGER_THREAD, Runtime.getRuntime().availableProcessors() * 2 - 1));
        threadMap.put(TaskManager.BACKGROUND_THREAD, background = new TaskThreadImpl(TaskManager.BACKGROUND_THREAD));
        threadMap.put(TaskManager.FOREGROUND_THREAD, foreground = new JavaFXTaskThreadImpl());
    }

    @Override
    public <T> CompletableFutureSupplier<T> createCompletableFutureSupplier() {
        return new CompletableFutureSupplierImpl<>();
    }

    @Override
    public CompletableFuture createCompletableFuture() {
        return new CompletableFutureImpl();
    }

    @Override
    public DynamicTask createDynamicTask(Task task) {
        return new DynamicTaskImpl(task);
    }

    @Override
    public GroupTask groupTask(String name, List<? extends Task> tasks) {
        return new GroupTaskImpl(name, tasks);
    }

    @Override
    public Task createTask(String name, Runnable runnable) {
        return new TaskImpl(name, runnable);
    }

    @Override
    public WeakTask createWeakTask(Task task) {
        return new WeakTaskImpl(task);
    }

    @Override
    public TaskThread getThread(String name) {
        return threadMap.get(name);
    }

    @Override
    public <T> FutureSupplier<T> createFutureSupplier(TaskThread thread, Supplier<T> supplier) {
        CompletableFutureSupplier<T> future = new CompletableFutureSupplierImpl<>();
        thread.runTask(new TaskImpl("FutureSupplier", () -> {
            try {
                future.complete(supplier.get());
            } catch (Throwable t) {
                if (t instanceof ReportedError && t.getCause() != null) {
                    t = t.getCause();
                }
                future.error(t);
            }
        }));
        return future;
    }

    @Override
    public Future createFuture(TaskThread thread, Runnable runnable) {
        return new FutureImpl(thread, runnable);
    }

    @Override
    public Duration duration(long millis) {
        return new DurationImpl(millis);
    }

    @Override
    public TaskThread getBackgroundThread() {
        return background;
    }

    @Override
    public TaskThread getForegroundThread() {
        return foreground;
    }
}
