package thito.nodeflow.task.batch;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.StringProperty;
import thito.nodeflow.NodeFlow;
import thito.nodeflow.task.TaskThread;

import java.util.logging.Level;

public class TaskProgress {
    Batch batch;
    Progress mainProgress;
    Progress progress;
    Batch.Task task;
    int index = 0;
    Runnable onDone;

    public TaskProgress(Batch batch, Progress mainProgress, Batch.Task task, Runnable onDone) {
        this.onDone = onDone;
        this.batch = batch;
        this.mainProgress = mainProgress;
        this.task = task;
    }

    public void proceed() {
        if (task.next != null) {
            task = task.next;
            execute();
        } else {
            index = 0;
            if (onDone != null) {
                onDone.run();
            }
        }
    }

    void execute() {
        final Batch.Task task = this.task;
        task.thread.schedule(() -> {
            ProgressedTask progressedTask = task.progressedTask;
            progress = Progress.create();
            progress.setStatus(mainProgress.getStatus());
            mainProgress.statusProperty().bind(progress.statusProperty());
            mainProgress.progressProperty().bind(progress.progressProperty());
            if (progressedTask != null) {
                try {
                    progressedTask.run(this);
                } catch (Throwable t) {
                    NodeFlow.getLogger().log(Level.SEVERE, "Failed to do batch task", t);
                }
            }
            mainProgress.statusProperty().unbind();
            mainProgress.progressProperty().unbind();
            progress = null;
            mainProgress.setProgress(Math.max((++index) / (double) task.batch.count, mainProgress.getProgress()));
            if (!(task.progressedTask instanceof LazyProgressedTask)) {
                proceed();
            }
        });
    }

    public TaskProgress append(Batch.Task batch) {
        if (batch == null) return this;
//        batch = batch.cloneTasks(this.batch);
//        task.push(batch);
        append(TaskThread.BG(), batch);
        return this;
    }

    public TaskProgress append(TaskThread thread, ProgressedTask task) {
        this.task.push(new Batch.Task(batch, thread, task));
        return this;
    }

    public TaskProgress appendLazy(TaskThread thread, LazyProgressedTask task) {
        return append(thread, task);
    }

    public TaskProgress insert(Batch.Task batch) {
        if (batch == null) return this;
//        batch = batch.cloneTasks(this.batch);
//        task.insert(batch);
        insert(TaskThread.BG(), batch);
        return this;
    }

    public TaskProgress insert(TaskThread taskThread, ProgressedTask task) {
        this.task.insert(new Batch.Task(this.task.batch, taskThread, task));
        return this;
    }

    public TaskProgress insertLazy(TaskThread taskThread, LazyProgressedTask task) {
        return insert(taskThread, task);
    }

    public String getStatus() {
        return progress.getStatus();
    }

    public StringProperty statusProperty() {
        return progress.statusProperty();
    }

    public void setStatus(String status) {
        NodeFlow.getLogger().log(Level.INFO, status);
        progress.setStatus(status);
    }

    public double getProgress() {
        return progress.getProgress();
    }

    public DoubleProperty progressProperty() {
        return progress.progressProperty();
    }

    public void setProgress(double progress) {
        this.progress.setProgress(progress);
    }
}
