package thito.nodeflow.api.editor.menu;

import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.ui.action.*;
import thito.nodeflow.api.ui.*;

public interface ToolButton extends ToolControl {
    I18nItem getTooltip();
    Icon getIcon();
    void dispatchClick(ClickAction.MouseButton button);
}
