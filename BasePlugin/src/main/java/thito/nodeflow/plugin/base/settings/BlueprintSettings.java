package thito.nodeflow.plugin.base.settings;

import javafx.beans.property.*;
import thito.nodeflow.engine.node.*;
import thito.nodeflow.settings.SettingsCanvas;
import thito.nodeflow.settings.canvas.*;

@Category(value = "${plugin.blueprint.settings.name}", context = SettingsContext.ALL)
public class BlueprintSettings extends SettingsCanvas {

    @Item("${plugin.blueprint.settings.link-style}")
    public final ObjectProperty<LinkStyle> linkStyle = new SimpleObjectProperty<>(LinkStyle.CABLE);
}
