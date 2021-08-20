package thito.nodeflow.internal.settings.general;

import lombok.*;
import lombok.experimental.*;
import thito.nodeflow.internal.settings.*;
import thito.nodeflow.library.language.*;
import thito.nodeflow.library.ui.*;

import java.io.*;

@Getter @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class General implements Settings {

    @FileSettings(directory = true)
    SettingsProperty<File> workspaceDirectory = new SettingsProperty<>(I18n.$("settings.general.items.workspace-directory"), new File("Workspace").getAbsoluteFile());
    SettingsProperty<Boolean> showDashboardAtStart = new SettingsProperty<>(I18n.$("settings.general.items.show-dashboard-at-start"), true);
    SettingsProperty<Theme> theme = new SettingsProperty<>(I18n.$("settings.general.items.name"), new Theme("Dark"));
    SettingsProperty<Language> language = new SettingsProperty<>(I18n.$("settings.general.items.language"), new Language("en_us"));

    @NumberSettings(min = 0, max = 100)
    SettingsProperty<Integer> maxOpenTabs = new SettingsProperty<>(I18n.$("settings.general.items.max-opened-tabs"), 20);

    @NumberSettings(min = 0, max = 200)
    SettingsProperty<Integer> actionBuffer = new SettingsProperty<>(I18n.$("settings.general.items.action-buffer"), 50);

    public class Appearance implements Settings {
        @Override
        public SettingsDescription description() {
            return new SettingsDescription(I18n.$("settings.general.sub.appearance"));
        }
    }

    @Override
    public SettingsDescription description() {
        return new SettingsDescription(I18n.$("settings.general.name"));
    }
}
