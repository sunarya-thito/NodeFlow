package thito.nodeflow.api.task;

import java.util.List;

public interface GroupTask extends Task {
    double getProgress();

    List<? extends Task> getTasks();
}
