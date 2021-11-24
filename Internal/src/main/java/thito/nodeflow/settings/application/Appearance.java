package thito.nodeflow.settings.application;

import javafx.beans.property.*;
import thito.nodeflow.NodeFlow;
import thito.nodeflow.language.Language;
import thito.nodeflow.settings.SettingsCanvas;
import thito.nodeflow.settings.canvas.*;
import thito.nodeflow.ui.Theme;
import thito.nodeflow.ui.ThemeManager;

@Category(value = "${settings.appearance.name}", context = SettingsContext.GLOBAL)
public class Appearance extends SettingsCanvas {

    @Item("${settings.appearance.items.show-dashboard-at-start}")
    public final BooleanProperty showDashboardAtStart = new SimpleBooleanProperty(true);

    @Item("${settings.appearance.items.theme}")
    public final ObjectProperty<Theme> theme = new SimpleObjectProperty<>(new Theme("Dark"));

    @Item("${settings.appearance.items.language}")
    public final ObjectProperty<Language> language = new SimpleObjectProperty<>(NodeFlow.getInstance().getDefaultLanguage());

    {
        ThemeManager.getInstance().themeProperty().bindBidirectional(theme);
        Language.languageProperty().bindBidirectional(language);
    }
}