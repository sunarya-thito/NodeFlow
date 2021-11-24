package thito.nodeflow.task.thread;

import javafx.animation.*;
import javafx.application.*;
import javafx.beans.property.*;
import javafx.util.*;
import thito.nodeflow.task.*;

import java.util.concurrent.atomic.*;
import java.util.logging.*;

public class FXTaskThread implements TaskThread {

    private LongProperty timeInMillis = new SimpleLongProperty();

    public FXTaskThread() {
        schedule(() -> {
            Thread.currentThread().setName("UI");
        });
        schedule(() -> {
            timeInMillis.set(System.currentTimeMillis());
        }, Duration.millis(1), Duration.millis(1));
    }

    @Override
    public String getThreadName() {
        return "UI";
    }

    @Override
    public ScheduledTask schedule(Runnable runnable) {
        AtomicReference<TaskState> state = new AtomicReference<>(TaskState.IDLE);
        AtomicBoolean cancel = new AtomicBoolean(false);
        Platform.runLater(() -> {
            if (cancel.get()) {
                return;
            }
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
                state.set(TaskState.STOPPED);
                cancel.set(true);
            }

            @Override
            public TaskState getState() {
                return state.get();
            }

        };
    }

    @Override
    public LongProperty timeInMillisProperty() {
        return timeInMillis;
    }

    @Override
    public void shutdown() {
        Platform.exit();
    }

    @Override
    public ScheduledTask schedule(Runnable task, Duration delay) {
        AtomicReference<TaskState> state = new AtomicReference<>(TaskState.IDLE);
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(0), event -> {
            state.set(TaskState.RUNNING);
            try {
                task.run();
            } catch (Throwable t) {
                TaskManager.getLogger().log(Level.SEVERE, "Failed to schedule a task", t);
            }
            state.set(TaskState.STOPPED);
        }));
        timeline.setDelay(delay);
        timeline.play();
        return new ScheduledTask() {
            @Override
            public void cancel() {
                timeline.stop();
                state.set(TaskState.STOPPED);
            }

            @Override
            public TaskState getState() {
                return state.get();
            }
        };
    }

    @Override
    public ScheduledTask schedule(Runnable task, Duration delay, Duration period) {
        AtomicReference<TaskState> state = new AtomicReference<>(TaskState.IDLE);
        Timeline timeline = new Timeline(new KeyFrame(period, event -> {
            state.set(TaskState.RUNNING);
            try {
                task.run();
            } catch (Throwable t) {
                TaskManager.getLogger().log(Level.SEVERE, "Failed to schedule a task", t);
            }
            state.set(TaskState.IDLE);
        }));
        timeline.setDelay(delay);
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
        return new ScheduledTask() {
            @Override
            public void cancel() {
                timeline.stop();
                state.set(TaskState.STOPPED);
            }

            @Override
            public TaskState getState() {
                return state.get();
            }
        };
    }

    @Override
    public boolean isInThread() {
        return Platform.isFxApplicationThread();
    }
}
