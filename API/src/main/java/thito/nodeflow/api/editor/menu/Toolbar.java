package thito.nodeflow.api.editor.menu;

import javafx.scene.*;
import thito.nodeflow.api.editor.*;

public interface Toolbar {
    FileSession getEditorSession();
    ToolComponent[] getComponents();
    Node impl_getPeer();
}
