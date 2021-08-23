package thito.nodeflow.internal;

import javafx.beans.property.*;

public interface ProgressedTask {
    void run(DoubleProperty progress, StringProperty status);
}
