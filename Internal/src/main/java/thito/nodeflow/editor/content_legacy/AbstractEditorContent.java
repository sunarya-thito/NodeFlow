package thito.nodeflow.editor.content_legacy;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import thito.nodeflow.editor.Editor;

public abstract class AbstractEditorContent {
    private final Editor editor;
    private final EditorContentType type;
    private final BorderPane root = new BorderPane();
    protected final ObjectProperty<Node> content = root.centerProperty();

    public AbstractEditorContent(Editor editor, EditorContentType type) {
        this.editor = editor;
        this.type = type;
    }

    public EditorContentType getType() {
        return type;
    }

    public ObjectProperty<Node> contentProperty() {
        return content;
    }

    public Node getNode() {
        return root;
    }
}
