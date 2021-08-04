package thito.nodeflow.api.task;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;

public interface DynamicTask extends Task {
    ThreadLocal<DoubleProperty> PROGRESS = ThreadLocal.withInitial(() -> new SimpleDoubleProperty());
    ThreadLocal<ObjectProperty<Progress>> PROCESS = ThreadLocal.withInitial(() -> new SimpleObjectProperty<>());

    static void progress(int current, int max) {
        PROGRESS.get().set(max == 0 ? 1 : current / (double) max);
    }

    double getProgress();
}
