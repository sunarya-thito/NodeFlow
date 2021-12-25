package thito.nodeflow.ui.task;

import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import thito.nodeflow.ui.Component;
import thito.nodeflow.ui.Skin;

public class ProgressSkin extends Skin {
    @Component("status")
    Label status;

    @Component("progress")
    ProgressBar progressBar;

    public Label getStatus() {
        return status;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }
}
