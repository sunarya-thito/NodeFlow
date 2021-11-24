package thito.nodeflow.editor.content_legacy;

import thito.nodeflow.editor.Editor;

public interface EditorContentType {
    String id();
    AbstractEditorContent createElement(Editor editor);
}
