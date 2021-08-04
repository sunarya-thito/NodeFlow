package thito.nodeflow.api.settings;

public interface SettingsValidator {
    SettingsValidator and(SettingsValidator validator);
    SettingsValidator or(SettingsValidator validator);
    SettingsValidator xor(SettingsValidator validator);
    SettingsValidator not();
}
