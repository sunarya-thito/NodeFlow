package thito.nodeflow.plugin.base.settings;

import thito.nodeflow.engine.node.*;
import thito.nodeflow.internal.settings.*;
import thito.nodeflow.library.language.*;

public class BlueprintSettings implements Settings {

    public final SettingsProperty<LinkStyle> linkStyle = new SettingsProperty<>(I18n.$("baseplugin.blueprint.settings.link-style"), LinkStyle.CABLE);

    @Override
    public SettingsDescription description() {
        return new SettingsDescription(I18n.$("baseplugin.blueprint.settings.name"));
    }
}
