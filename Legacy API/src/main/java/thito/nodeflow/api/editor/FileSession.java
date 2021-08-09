package thito.nodeflow.api.editor;

import javafx.scene.*;
import thito.nodeflow.api.editor.menu.*;
import thito.nodeflow.api.resource.*;

public interface FileSession {
    UndoManager getUndoManager();
    FileHandler getHandler();
    ResourceFile getFile();
    Node impl_getViewport();
    Toolbar getToolbar();
}
