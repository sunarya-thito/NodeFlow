package thito.nodeflow.library.ui.decoration.dialog;

import javafx.stage.*;
import thito.nodeflow.internal.*;

public class DialogBase {
    private Stage stage;

    public DialogBase(Window owner) {
        stage = new Stage(StageStyle.TRANSPARENT);
        stage.setResizable(false);
        stage.setTitle("Dialog");
        stage.initOwner(owner);
        stage.initModality(owner == null ? Modality.APPLICATION_MODAL : Modality.WINDOW_MODAL);
        stage.addEventHandler(WindowEvent.WINDOW_SHOWN, event -> {
            Toolkit.centerOnScreen(stage);
            stage.sizeToScene();
        });
        Toolkit.centerOnScreen(stage);
    }

    public Stage getStage() {
        return stage;
    }
}
