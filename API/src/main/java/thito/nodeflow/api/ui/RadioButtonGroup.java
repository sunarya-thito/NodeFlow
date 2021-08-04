package thito.nodeflow.api.ui;

import thito.nodeflow.api.NodeFlow;

import java.util.List;

public interface RadioButtonGroup {
    RadioButtonGroup MENU_BAR_WINDOW_GROUP = createGroup("menu_bar_window_group", 1);
    RadioButtonGroup SELECT_MODE = createGroup("node_select_mode", 1);
    static RadioButtonGroup createGroup(String name, int maxAmount) {
        return NodeFlow.getApplication().getToolkit().createGroup(name, maxAmount);
    }
    static RadioButtonGroup createGroup(String name) {
        return createGroup(name, 1);
    }
    String getName();
    int getMaximumAmount();
    List<RadioButton> getSelected();
    void attemptSelect(RadioButton button);
    void attemptUnSelect(RadioButton button);
}
