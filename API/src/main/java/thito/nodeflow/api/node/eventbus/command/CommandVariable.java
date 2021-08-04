package thito.nodeflow.api.node.eventbus.command;

import thito.nodeflow.api.locale.*;

public class CommandVariable {
    private I18nItem displayName;
    private Class<?> type;

    public CommandVariable(I18nItem displayName, Class<?> type) {
        this.displayName = displayName;
        this.type = type;
    }

    public I18nItem getDisplayName() {
        return displayName;
    }
    public Class<?> getType() {
        return type;
    }
}
