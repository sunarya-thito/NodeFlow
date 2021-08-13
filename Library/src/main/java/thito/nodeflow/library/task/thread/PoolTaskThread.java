package thito.nodeflow.library.task.thread;

import javafx.util.*;
import thito.nodeflow.library.task.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.logging.*;

public class PoolTaskThread implements TaskThread {
    private String name;
    private Thread thread;
    private ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor(r -> thread = new Thread(r, name));

    public PoolTaskThread(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean isInThread() {
        return Thread.currentThread() == thread;
    }

    @Override
    public void shutdown() {
        service.shutdown();
        try {
            service.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        }
    }

    @Override
    public ScheduledTask schedule(Runnable runnable) {
        AtomicReference<TaskState> state = new AtomicReference<>(TaskState.IDLE);
        Future<?> future = service.submit(() -> {
            state.set(TaskState.RUNNING);
            try {
                runnable.run();
            } catch (Throwable t) {
                TaskManager.getLogger().log(Level.SEVERE, "Failed to schedule a task", t);
            }
            state.set(TaskState.STOPPED);
        });
        return new ScheduledTask() {
            @Override
            public void cancel() {
                future.cancel(false);
                state.set(TaskState.STOPPED);
            }

            @Override
            public TaskState getState() {
                return state.get();
            }
        };
    }

    @Override
    public ScheduledTask schedule(Runnable runnable, Duration delay) {
        AtomicReference<TaskState> state = new AtomicReference<>(TaskState.IDLE);
        ScheduledFuture<?> future = service.schedule(() -> {
            state.set(TaskState.RUNNING);
            try {
                runnable.run();
            } catch (Throwable t) {
                TaskManager.getLogger().log(Level.SEVERE, "Failed to schedule a task", t);
            }
            state.set(TaskState.STOPPED);
        }, (long) delay.toMillis(), TimeUnit.MILLISECONDS);
        return new ScheduledTask() {
            @Override
            public void cancel() {
                future.cancel(false);
                state.set(TaskState.STOPPED);
            }

            @Override
            public TaskState getState() {
                return state.get();
            }
        };
    }

    @Override
    public ScheduledTask schedule(Runnable runnable, Duration delay, Duration period) {
        AtomicReference<TaskState> state = new AtomicReference<>(TaskState.IDLE);
        ScheduledFuture<?> future = service.scheduleAtFixedRate(() -> {
            state.set(TaskState.RUNNING);
            try {
                runnable.run();
            } catch (Throwable t) {
                TaskManager.getLogger().log(Level.SEVERE, "Failed to schedule a task", t);
            }
            state.set(TaskState.IDLE);
        }, (long) delay.toMillis(), (long) period.toMillis(), TimeUnit.MILLISECONDS);
        return new ScheduledTask() {
            @Override
            public void cancel() {
                future.cancel(false);
                state.set(TaskState.STOPPED);
            }

            @Override
            public TaskState getState() {
                return state.get();
            }
        };
    }
}
