package thito.nodeflow.internal.task.thread;

import javafx.animation.*;
import javafx.application.*;
import javafx.util.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.internal.task.*;

import java.util.concurrent.atomic.*;
import java.util.logging.*;

public class FXTaskThread implements TaskThread {
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
                NodeFlow.getLogger().log(Level.SEVERE, "Failed to schedule a task", t);
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
    public ScheduledTask schedule(Runnable task, Duration delay) {
        AtomicReference<TaskState> state = new AtomicReference<>(TaskState.IDLE);
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(0), event -> {
            state.set(TaskState.RUNNING);
            try {
                task.run();
            } catch (Throwable t) {
                NodeFlow.getLogger().log(Level.SEVERE, "Failed to schedule a task", t);
            }
            state.set(TaskState.STOPPED);
        }));
        timeline.setDelay(delay);
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
                NodeFlow.getLogger().log(Level.SEVERE, "Failed to schedule a task", t);
            }
            state.set(TaskState.IDLE);
        }));
        timeline.setDelay(delay);
        timeline.setCycleCount(Animation.INDEFINITE);
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
}
