package thito.nodeflow.task;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BatchTask implements ProgressedTask {
    private List<ProgressedTask> taskList = new ArrayList<>();

    public void submitTask(ProgressedTask task) {
        if (taskList == null) throw new IllegalStateException("already scheduled to run");
        taskList.add(task);
    }

    @Override
    public void run(Progress progress) {
        start(null, progress);
    }

    public void start(TaskThread thread, Progress progress) {
        int maxProgress = taskList.size();
        start(thread, progress, maxProgress, 0, taskList.iterator());
        taskList = null;
    }

    private void start(TaskThread thread, Progress progress, int maxProgress, int count, Iterator<ProgressedTask> iterator) {
        if (iterator.hasNext()) {
            ProgressedTask task = iterator.next();
            if (thread == null) {
                runTaskLinked(null, progress, maxProgress, count, iterator, task);
            } else {
                thread.schedule(() -> {
                    runTaskLinked(thread, progress, maxProgress, count, iterator, task);
                });
            }
        }
    }

    private void runTaskLinked(TaskThread thread, Progress progress, int maxProgress, int count, Iterator<ProgressedTask> iterator, ProgressedTask task) {
        DoubleProperty currentProgress = new SimpleDoubleProperty();
        progress.progressProperty().bind(currentProgress.divide(maxProgress).add(count / (double) maxProgress));
        try {
            task.run(new Progress(progress.statusProperty(), currentProgress));
        } catch (Throwable t) {
            t.printStackTrace();
        }
        progress.progressProperty().unbind();
        progress.setProgress((count + 1) / (double) maxProgress);
        start(thread, progress, maxProgress, count + 1, iterator);
    }
}
