package thito.nodeflow.internal.ui;

import javafx.scene.control.*;
import javafx.scene.layout.*;

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
