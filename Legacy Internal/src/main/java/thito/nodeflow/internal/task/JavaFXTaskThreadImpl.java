package thito.nodeflow.internal.task;

import javafx.animation.*;
import javafx.application.*;
import thito.nodeflow.api.task.*;

import java.util.*;

public class JavaFXTaskThreadImpl implements TaskThread {

    private final List<Task> tasks = new ArrayList<>();

    public JavaFXTaskThreadImpl() {
    }

    @Override
    public String getName() {
        return "Foreground";
    }

    @Override
    public List<Task> getActiveTasks() {
        return tasks;
    }

    @Override
    public Task runTask(Task task) {
        Platform.runLater(() -> {
            tasks.add(task);
            if (task instanceof AbstractTaskImpl) {
                ((AbstractTaskImpl) task).setState(TaskState.RUNNING);
            }
            try {
                if (task instanceof AbstractTaskImpl) {
                    ((AbstractTaskImpl) task).run();
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
            if (task instanceof AbstractTaskImpl) {
                ((AbstractTaskImpl) task).setState(TaskState.DONE);
            }
            tasks.remove(task);
        });
        return task;
    }

    @Override
    public Task runTaskRepeatedly(Task task, Duration delay, Duration period) {
        Timeline timeline = new Timeline(new KeyFrame(javafx.util.Duration.millis(delay.asMillis()), event -> {
            tasks.add(task);
            if (task instanceof AbstractTaskImpl) {
                ((AbstractTaskImpl) task).setState(TaskState.RUNNING);
                ((AbstractTaskImpl) task).setThread(this);
            }
            try {
                if (task instanceof AbstractTaskImpl) {
                    ((AbstractTaskImpl) task).run();
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
            if (task instanceof AbstractTaskImpl) {
                ((AbstractTaskImpl) task).setState(TaskState.DONE);
            }
            tasks.remove(task);
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
        return task;
    }

    @Override
    public Task runTaskLater(Task task, Duration delay) {
        new Timeline(new KeyFrame(javafx.util.Duration.millis(delay.asMillis()), event -> {
            tasks.add(task);
            if (task instanceof AbstractTaskImpl) {
                ((AbstractTaskImpl) task).setState(TaskState.RUNNING);
            }
            try {
                if (task instanceof AbstractTaskImpl) {
                    ((AbstractTaskImpl) task).run();
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
            if (task instanceof AbstractTaskImpl) {
                ((AbstractTaskImpl) task).setState(TaskState.DONE);
            }
            tasks.remove(task);
        })).play();
        return task;
    }
}
