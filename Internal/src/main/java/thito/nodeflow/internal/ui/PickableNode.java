package thito.nodeflow.internal.ui;

import javafx.beans.property.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;

import java.util.*;

public class PickableNode<T extends Node> extends Pane {
    private static DataFormat clipboardKey = new DataFormat("PickableNode");
    private static Map<UUID, Node> clipboardMap = new HashMap<>();
    private Class<T> type;

    private ObjectProperty<T> content = new SimpleObjectProperty<>();
    private ObjectProperty<Node> placeholder = new SimpleObjectProperty<>();

    public PickableNode(Class<T> type) {
        this.type = type;
        addEventHandler(MouseEvent.DRAG_DETECTED, event -> {
            event.consume();
            Node content = getContent();
            if (content == null) {
                return;
            }
            Dragboard board = startDragAndDrop(TransferMode.MOVE);

            Bounds bounds = content.getLayoutBounds();
            WritableImage image = new WritableImage((int) bounds.getWidth(), (int) bounds.getHeight());
            image = content.snapshot(null, image);

            board.setDragView(image);

            ClipboardContent clipboard = new ClipboardContent();
            UUID key = UUID.randomUUID();
            clipboardMap.put(key, content);
            clipboard.put(clipboardKey, key.toString());
            placeholder.set(requestPlaceholder());
            setContent(null);
            board.setContent(clipboard);
        });
        addEventHandler(DragEvent.DRAG_DONE, event -> {
            placeholder.set(null);
        });
        addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            event.consume();
            event.setDragDetect(true);
        });
        addEventHandler(DragEvent.DRAG_OVER, event -> {
            if (event.getGestureSource() != this && event.getDragboard().hasContent(clipboardKey)) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });
        addEventHandler(DragEvent.DRAG_DROPPED, event -> {
            Dragboard db = event.getDragboard();
            if (db.hasContent(clipboardKey)) {
                UUID uuid = UUID.fromString((String) db.getContent(clipboardKey));
                Node result = clipboardMap.remove(uuid);
                contentProperty().set((T) result);
                event.setDropCompleted(result != null);
            } else {
                event.setDropCompleted(false);
            }
            event.consume();
        });
    }

    public T getContent() {
        return content.get();
    }

    public ObjectProperty<T> contentProperty() {
        return content;
    }

    public void setContent(T content) {
        this.content.set(content);
    }

    protected Node requestPlaceholder() {
        T content = getContent();
        Pane pane = new Pane();
        pane.getStyleClass().add("pickable-node-placeholder");
        Bounds bounds = content.getLayoutBounds();
        pane.setMinWidth(bounds.getWidth());
        pane.setMinHeight(bounds.getHeight());
        return pane;
    }
}
