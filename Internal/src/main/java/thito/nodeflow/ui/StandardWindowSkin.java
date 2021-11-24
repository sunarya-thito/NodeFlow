package thito.nodeflow.ui;

import javafx.scene.control.*;
import javafx.scene.layout.*;

public class StandardWindowSkin extends Skin {
    @Component("caption")
    Pane caption;
    @Component("root")
    BorderPane root;
    @Component("window-title")
    Labeled title;

    private StandardWindow window;

    public StandardWindowSkin(StandardWindow window) {
        this.window = window;
    }

    @Override
    protected void onLayoutLoaded() {
        title.textProperty().bind(window.getStage().titleProperty());
        root.centerProperty().set(window.getContent());
    }
}
