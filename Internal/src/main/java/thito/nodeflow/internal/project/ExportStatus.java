package thito.nodeflow.internal.project;

import javafx.beans.property.*;

public class ExportStatus {
    private StringProperty status = new SimpleStringProperty();
    private DoubleProperty progress = new SimpleDoubleProperty();

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
