package thito.nodeflow.internal.settings;

import thito.nodeflow.library.language.*;

public class SettingsDescription {
    private I18n name;

    public SettingsDescription(I18n name) {
        this.name = name;
    }

    public String getName() {
        return name.get();
    }
}
