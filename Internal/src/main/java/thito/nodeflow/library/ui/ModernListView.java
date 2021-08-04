package thito.nodeflow.library.ui;

import javafx.collections.*;
import javafx.scene.*;
import javafx.scene.layout.*;

public class ModernListView extends BorderPane {

    private ModernScrollPane scrollPane = new ModernScrollPane();
    private ListViewport viewport = new ListViewport();

    private ObservableList<Node> items = viewport.getChildren();

    public ModernListView() {
        scrollPane.getChildren().add(viewport);
        setCenter(scrollPane);
    }

    public ObservableList<Node> getItems() {
        return items;
    }

    private class ListViewport extends VBox {
    }
}
