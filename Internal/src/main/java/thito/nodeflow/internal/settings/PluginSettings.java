package thito.nodeflow.internal.settings;

import lombok.*;
import lombok.experimental.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.internal.plugin.*;
import thito.nodeflow.library.language.*;

import java.lang.reflect.*;

@Getter @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PluginSettings implements Settings {
    Plugin plugin;
    SettingsProperty<Boolean> enabled = new SettingsProperty<>(I18n.$("settings.plugins.enable"), true);

    public PluginSettings(Plugin plugin) {
        this.plugin = plugin;
    }

    public void addCategories(Class<? extends Settings>... settings) {
        for (Class<? extends Settings> s : settings) {
            try {
                SettingsCategory category = NodeFlow.getInstance().getSettingsManager().scan(this, enabled.getCategory(), s);
                enabled.getCategory().getSubCategory().add(category);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public SettingsDescription description() {
        return new SettingsDescription(I18n.direct(plugin.getName()));
    }
}
