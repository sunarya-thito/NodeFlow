package thito.nodeflow.internal.settings;

import thito.nodeflow.internal.*;

public interface Settings {
    static <T extends Settings> T get(Class<T> type) {
        return NodeFlow.getInstance().getSettingsManager().getSettings(type).orElseThrow();
    }
    SettingsDescription description();
}
