package thito.nodeflow.internal.ui.editor;

import javafx.scene.control.*;
import thito.nodeflow.library.ui.*;
import thito.nodeflow.library.ui.Skin;

public class LoadingTabSkin extends Skin {
    @Component("progress")
    ProgressBar progressBar;

    public ProgressBar getProgressBar() {
        return progressBar;
    }
}
