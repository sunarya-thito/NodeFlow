package thito.nodeflow.internal.settings.general;

import lombok.*;
import lombok.experimental.*;
import thito.nodeflow.internal.settings.*;
import thito.nodeflow.library.language.*;
import thito.nodeflow.library.ui.*;

import java.io.*;

@Getter @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class GeneralSettings implements Settings {

    SettingsProperty<File> workspaceDirectory = new SettingsProperty<>(I18n.$("settings.general.items.workspace-directory"), new File("Workspace").getAbsoluteFile());
    SettingsProperty<Boolean> showDashboardAtStart = new SettingsProperty<>(I18n.$("settings.general.items.show-dashboard-at-start"), true);
    SettingsProperty<Theme> theme = new SettingsProperty<>(I18n.$("settings.general.items.name"), new Theme("Dark"));
    SettingsProperty<Language> language = new SettingsProperty<>(I18n.$("settings.general.items.language"), new Language("en_us"));
    SettingsProperty<Integer> actionBuffer = new SettingsProperty<>(I18n.$("settings.general.items.action-buffer"), 50);

    @Override
    public SettingsDescription description() {
        return new SettingsDescription(I18n.$("settings.appearance.name"));
    }
}
