package thito.nodeflow.internal;

import javafx.beans.property.*;

public interface PreLoadTask {
    void run(DoubleProperty progress, StringProperty status);
}
