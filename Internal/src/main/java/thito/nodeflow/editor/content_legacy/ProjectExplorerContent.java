package thito.nodeflow.editor.content_legacy;

import thito.nodeflow.editor.Editor;

public class ProjectExplorerContent extends AbstractEditorContent {

    public static class Type implements EditorContentType {
        @Override
        public String id() {
            return "project.explorer";
        }

        @Override
        public AbstractEditorContent createElement(Editor editor) {
            return new ProjectExplorerContent(editor, this);
        }
    }

    public ProjectExplorerContent(Editor editor, Type type) {
        super(editor, type);
    }
}
