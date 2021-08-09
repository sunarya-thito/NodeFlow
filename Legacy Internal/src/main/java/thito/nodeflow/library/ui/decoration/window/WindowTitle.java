package thito.nodeflow.library.ui.decoration.window;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import thito.nodeflow.internal.*;

public class WindowTitle {

    private Label title = new Label();
    private HBox pane = new HBox();
    private WindowBar bar;

    public WindowTitle(WindowBar bar) {
        this.bar = bar;
        title.textProperty().bind(bar.getBase().stage.titleProperty());
        Toolkit.style(pane, "window-title-bar");
        Toolkit.style(title, "window-title");
        pane.getChildren().addAll(title);
    }

    HBox getPane() {
        return pane;
    }
}
