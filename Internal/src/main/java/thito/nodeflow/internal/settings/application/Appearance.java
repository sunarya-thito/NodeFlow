package thito.nodeflow.internal.settings.application;

import javafx.beans.property.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.internal.binding.*;
import thito.nodeflow.internal.language.*;
import thito.nodeflow.internal.settings.*;
import thito.nodeflow.internal.settings.canvas.*;
import thito.nodeflow.internal.ui.*;

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