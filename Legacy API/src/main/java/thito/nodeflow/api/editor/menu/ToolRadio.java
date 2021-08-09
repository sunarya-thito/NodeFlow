package thito.nodeflow.api.editor.menu;

import thito.nodeflow.api.ui.*;

public interface ToolRadio extends ToolButton, RadioButton {
    default ToolRadio select() {
        setSelected(true);
        return this;
    }
}
