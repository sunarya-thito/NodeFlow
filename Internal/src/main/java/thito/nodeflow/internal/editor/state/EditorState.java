package thito.nodeflow.internal.editor.state;

import org.dockfx.DockPos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditorState implements Serializable {
    public List<String> openedFilePaths = new ArrayList<>();
    public Map<String, DockPos> dockingPositionMap = new HashMap<>();

    public EditorState() {
    }
}
