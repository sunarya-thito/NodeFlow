package thito.nodeflow.ui.editor;

import java.io.Serial;
import java.io.Serializable;

public class EditorWindowState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    public double x, y, width, height;
    public boolean iconified, maximized;
}
