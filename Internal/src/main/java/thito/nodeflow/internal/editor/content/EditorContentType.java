package thito.nodeflow.internal.editor.content;

import org.dockfx.DockPos;
import thito.nodeflow.internal.editor.Editor;

public interface EditorContentType {
    String id();
    AbstractEditorContent createElement(Editor editor);
}
