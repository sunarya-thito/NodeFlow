package thito.nodeflow.task.batch;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Progress {
    public static Progress create() {
        return new Progress(new SimpleStringProperty(), new SimpleDoubleProperty());
    }
    private StringProperty status;
    private DoubleProperty progress;

    public Progress(StringProperty status, DoubleProperty progress) {
        this.status = status;
        this.progress = progress;
    }

    public Progress() {
        this(new SimpleStringProperty(), new SimpleDoubleProperty());
    }

    public String getStatus() {
        return status.get();
    }

    public StringProperty statusProperty() {
        return status;
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public double getProgress() {
        return progress.get();
    }

    public DoubleProperty progressProperty() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress.set(progress);
    }
}
