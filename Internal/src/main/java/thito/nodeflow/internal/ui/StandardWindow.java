package thito.nodeflow.internal.ui;

import javafx.beans.property.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import thito.nodeflow.library.ui.*;

public class StandardWindow extends Window {
    private BorderPane content = new BorderPane();

    public ObjectProperty<Node> contentProperty() {
        return content.centerProperty();
    }

    @Override
    protected Skin createSkin() {
        return null;
    }

    @Override
    protected WindowHitTest createHitTest() {
        return null;
    }
}
