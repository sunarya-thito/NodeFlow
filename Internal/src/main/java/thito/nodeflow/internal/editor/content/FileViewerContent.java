package thito.nodeflow.internal.editor.content;

import org.dockfx.DockPos;
import thito.nodeflow.internal.editor.Editor;

public class FileViewerContent extends AbstractEditorContent {
    public static class Type implements EditorContentType {
        @Override
        public String id() {
            return null;
        }

        @Override
        public AbstractEditorContent createElement(Editor editor) {
            return new FileViewerContent(editor, this);
        }
    }

    public FileViewerContent(Editor editor, EditorContentType type) {
        super(editor, type);
    }
}
