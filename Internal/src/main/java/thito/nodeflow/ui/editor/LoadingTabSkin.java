package thito.nodeflow.ui.editor;

import javafx.scene.control.*;
import thito.nodeflow.ui.Component;
import thito.nodeflow.ui.Skin;

public class LoadingTabSkin extends Skin {
    @Component("progress")
    ProgressBar progressBar;

    public ProgressBar getProgressBar() {
        return progressBar;
    }
}
