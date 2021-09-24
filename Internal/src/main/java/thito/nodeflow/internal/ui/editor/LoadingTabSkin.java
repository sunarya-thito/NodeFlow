package thito.nodeflow.internal.ui.editor;

import javafx.scene.control.*;
import thito.nodeflow.internal.ui.*;
import thito.nodeflow.internal.ui.Skin;

public class LoadingTabSkin extends Skin {
    @Component("progress")
    ProgressBar progressBar;

    public ProgressBar getProgressBar() {
        return progressBar;
    }
}
