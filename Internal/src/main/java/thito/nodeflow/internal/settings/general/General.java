package thito.nodeflow.internal.settings.general;

import lombok.*;
import lombok.experimental.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.internal.project.*;
import thito.nodeflow.internal.settings.*;
import thito.nodeflow.library.language.*;
import thito.nodeflow.library.ui.*;

import java.io.*;

@Getter @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class General implements Settings {

    @FileSettings(directory = true)
    SettingsProperty<File> workspaceDirectory = new SettingsProperty<>(I18n.$("settings.general.items.workspace-directory"), new File(NodeFlow.ROOT, "Workspace").getAbsoluteFile());

    @NumberSettings(min = 0, max = 100)
    SettingsProperty<Integer> maxOpenTabs = new SettingsProperty<>(I18n.$("settings.general.items.max-opened-tabs"), 20);

    @NumberSettings(min = 0, max = 200)
    SettingsProperty<Integer> actionBuffer = new SettingsProperty<>(I18n.$("settings.general.items.action-buffer"), 50);

    public General() {
        workspaceDirectory.addListenerAndFire((obs, old, val) -> {
            val.mkdirs();
            Workspace workspace = new Workspace(val);
            NodeFlow.getInstance().workspaceProperty().set(workspace);
        });
    }

    @Getter @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    public class Appearance implements Settings {

        SettingsProperty<Boolean> showDashboardAtStart = new SettingsProperty<>(I18n.$("settings.general.sub.appearance.items.show-dashboard-at-start"), true);
        SettingsProperty<Theme> theme = new SettingsProperty<>(I18n.$("settings.general.sub.appearance.items.theme"), new Theme("Dark"));
        SettingsProperty<Language> language = new SettingsProperty<>(I18n.$("settings.general.sub.appearance.items.language"), NodeFlow.getInstance().getDefaultLanguage());

        public Appearance() {
            theme.addListenerAndFire((obs, old, val) -> {
                ThemeManager.getInstance().setTheme(val);
            });
            language.addListenerAndFire((obs, old, val) -> {
                Language.setLanguage(val);
            });
        }

        @Override
        public SettingsDescription description() {
            return new SettingsDescription(I18n.$("settings.general.sub.appearance.name"));
        }

        @Getter @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
        public class ColorScheme implements Settings {

            SettingsProperty<ColorValues> palette = new SettingsProperty<>(I18n.$("settings.general.sub.appearance.sub.color-scheme.items.palette"), new ColorValues(ThemeManager.getInstance().getColorPalette().getColorMap()));

            public ColorScheme() {
                palette.addListener((obs, old, val) -> {
                    ThemeManager.getInstance().getColorPalette().overrideMapProperty().set(val.getColorMap());
                });
            }

            @Override
            public SettingsDescription description() {
                return new SettingsDescription(I18n.$("settings.general.sub.appearance.sub.color-scheme.name"));
            }
        }
    }

    @Override
    public SettingsDescription description() {
        return new SettingsDescription(I18n.$("settings.general.name"));
    }
}
