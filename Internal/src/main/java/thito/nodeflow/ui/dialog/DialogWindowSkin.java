package thito.nodeflow.ui.dialog;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import thito.nodeflow.ui.Component;
import thito.nodeflow.ui.Skin;

public class DialogWindowSkin extends Skin {
    @Component("caption")
    Pane caption;
    @Component("root")
    BorderPane root;
    @Component("window-title")
    Labeled title;

    private DialogWindow window;

    public DialogWindowSkin(DialogWindow window) {
        this.window = window;
    }

    @Override
    protected void onLayoutLoaded() {
        title.textProperty().bind(window.getStage().titleProperty());
        root.setCenter(window.getContent());
    }
}
