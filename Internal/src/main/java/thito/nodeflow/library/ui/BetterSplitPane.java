package thito.nodeflow.library.ui;

import javafx.collections.*;
import javafx.scene.*;
import javafx.scene.control.*;

public class BetterSplitPane extends SplitPane {

    public BetterSplitPane() {
        getItems().addListener((ListChangeListener<Node>) c -> {
            while (c.next()) {
                for (Node added : c.getAddedSubList()) {
                    getChildren().add(added);
                }
                for (Node removed : c.getRemoved()) {
                    getChildren().remove(removed);
                }
            }
        });
    }
}
