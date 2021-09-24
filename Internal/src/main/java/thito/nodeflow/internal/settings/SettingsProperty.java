package thito.nodeflow.internal.settings;

import javafx.beans.property.*;
import thito.nodeflow.config.*;

public class SettingsProperty<T> extends SimpleObjectProperty<T> {
    private SettingsItem<T> settingsItem;

    public SettingsProperty(SettingsItem<T> settingsItem) {
        this.settingsItem = settingsItem;
    }

    public SettingsProperty(T initialValue, SettingsItem<T> settingsItem) {
        super(initialValue);
        this.settingsItem = settingsItem;
    }

    public SettingsItem<T> getSettingsItem() {
        return settingsItem;
    }

    public void apply() {
        settingsItem.setValue(get());
    }

    public void load(Section section, String key) {
        SettingsParser<T> parser = SettingsManager.getSettingsManager().getParser(settingsItem.getType());
        set(parser.fromConfig(section, key).orElse(null));
    }

    public void save(Section section, String key) {
        SettingsParser<T> parser = SettingsManager.getSettingsManager().getParser(settingsItem.getType());
        parser.toConfig(section, key, get());
    }
}
