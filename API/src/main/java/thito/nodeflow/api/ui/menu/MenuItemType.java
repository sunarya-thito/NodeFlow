package thito.nodeflow.api.ui.menu;

import thito.nodeflow.api.NodeFlow;
import thito.nodeflow.api.ui.menu.type.ButtonMenuItemType;
import thito.nodeflow.api.ui.menu.type.CheckBoxMenuItemType;
import thito.nodeflow.api.ui.menu.type.RadioButtonMenuItemType;

public interface MenuItemType {
    ButtonMenuItemType BUTTON_TYPE = NodeFlow.getApplication().getToolkit().menuButtonType();
    CheckBoxMenuItemType CHECKBOX_TYPE = NodeFlow.getApplication().getToolkit().menuCheckBoxType();
    RadioButtonMenuItemType RADIO_BUTTON_TYPE = NodeFlow.getApplication().getToolkit().menuRadioButtonType();
    MenuItemHandler createHandler(MenuItem item);
}
