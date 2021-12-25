package thito.nodeflow.task.batch;

import javafx.beans.property.*;
import thito.nodeflow.task.TaskThread;

import java.util.LinkedList;

public class TaskQueue {
    private LinkedList<Batch.Task> linkedList = new LinkedList<>();
    private ObjectProperty<Progress> progress = new SimpleObjectProperty<>();

    public void executeBatch(Batch.Task task) {
        TaskThread.UI().schedule(() -> {
            if (progress.get() != null) {
                TaskThread.BG().schedule(() -> {
                    linkedList.add(task);
                });
                return;
            }
            Progress pr = Progress.create();
            progress.set(pr);
            Batch.execute(TaskThread.BG(), task)
                .execute(TaskThread.BG(), p2 -> {
                    Batch.Task t;
                    while ((t = linkedList.poll()) != null) {
                        p2.append(t);
                    }
                    p2.append(TaskThread.UI(), p3 -> {
                        progress.set(null);
                    });
                }).start(pr);
        });
    }

    public ObjectProperty<Progress> progressProperty() {
        return progress;
    }
}
