package thito.nodeflow.internal.task;

import thito.nodeflow.api.*;
import thito.nodeflow.api.task.*;
import thito.nodeflow.internal.ui.resourcemonitor.*;

import java.lang.reflect.*;

public abstract class AbstractTaskImpl implements Task, Runnable {
    static Method ThreadLocal_createMap;
    static Method ThreadLocal_getMap;
    static Class<?> ThreadLocalMap;
    private boolean cancel;
    private final String name;
    private final ThreadLocal<TaskThread> thread = new ThreadLocal<>();
    private final ThreadLocal<TaskState> state = ThreadLocal.withInitial(() -> TaskState.IDLE);
    private final ThreadLocal<Long> start = ThreadLocal.withInitial(() -> 0L);
    private final ThreadLocal<Long> elapsed = ThreadLocal.withInitial(() -> 0L);
    private RuntimeException continuity;

    public AbstractTaskImpl(String name) {
        this.name = name;
        continuity = new RuntimeException();
        continuity.fillInStackTrace();
    }

    public void setState(Thread thread, TaskState state) {
    }
    public void setState(TaskState state) {
        this.state.set(state);
    }

    public void run() {
        try {
            setState(TaskState.RUNNING);
            if (!getName().equals("ticker")) {
                ResourceMonitorWindow.push("[RUN] "+getName()+" ("+(thread.get() == null ? "Unknown" : thread.get().getName())+")");
            }
            start.set(System.currentTimeMillis());
            runTask();
            if (elapsed.get() != 0) {
                elapsed.set((elapsed.get() + (System.currentTimeMillis() - start.get())) / 2);
            } else {
                elapsed.set(System.currentTimeMillis() - start.get());
            }
            setState(TaskState.DONE);
            if (!getName().equals("ticker")) {
                ResourceMonitorWindow.push("[DONE] "+getName()+" ("+elapsed.get()+"ms)");
            }
        } catch (Throwable t) {
            ReportedError error = new ReportedError(t);
            error.setStackTrace(continuity.getStackTrace());
            throw error;
        }
    }

    public abstract void runTask();

    @Override
    public void setupThread(TaskThread thread) {
        if (this.thread.get() == null) {
            setThread(thread);
        } else throw new IllegalStateException("already in a thread");
    }

    @Override
    public TaskThread getThread() {
        return thread.get();
    }

    public void setThread(TaskThread thread) {
        this.thread.set(thread);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public TaskState getState() {
        return state.get();
    }

    @Override
    public Duration getAverageRunDuration() {
        return Duration.millis(elapsed.get());
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void cancel() {
        cancel = true;
    }
}
