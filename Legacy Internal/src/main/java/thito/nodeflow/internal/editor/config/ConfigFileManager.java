package thito.nodeflow.internal.editor.config;

import thito.nodeflow.api.*;
import thito.nodeflow.api.bundle.*;
import thito.nodeflow.api.bundle.java.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.internal.editor.config.savemodes.*;
import thito.nodeflow.internal.editor.config.type.*;

import java.util.*;

public class ConfigFileManager {
    private static final ConfigFileManager manager = new ConfigFileManager();

    public static ConfigFileManager getManager() {
        return manager;
    }

    private DisabledSaveMode disabledSaveMode = new DisabledSaveMode();
    private ObjectValueType defaultType = new ObjectValueType(Object.class, I18n.$("config-object-type"));
    private Set<ConfigValueType> valueTypes = new HashSet<>();
    private List<ConfigFileSaveMode> saveModes = new ArrayList<>();

    public ConfigFileManager() {
        registerValueType(new StringValueType());
        registerValueType(new NumberValueType());
        registerValueType(new BooleanValueType());
        getSaveModes().add(disabledSaveMode);
        getSaveModes().add(new YamlFileSaveMode());
        getSaveModes().add(new ObjectFileSaveMode());
        for (Bundle loaded : NodeFlow.getApplication().getBundleManager().getLoadedBundles()) {
            if (loaded instanceof JavaBundle) {
                for (String name : ((JavaBundle) loaded).getAvailableClasses()) {
                    Class<?> cl = ((JavaBundle) loaded).findClass(name);
                    if (cl != null) {
                        registerValueType(new ObjectValueType(cl, I18n.direct(cl.getName())));
                    }
                }
            }
        }
    }

    public ConfigFileSaveMode getSaveMode(String id) {
        if (id != null) {
            for (ConfigFileSaveMode mode : saveModes) {
                if (mode.getId().equals(id)) {
                    return mode;
                }
            }
        }
        return disabledSaveMode;
    }

    public List<ConfigFileSaveMode> getSaveModes() {
        return saveModes;
    }

    public Set<ConfigValueType> getValueTypes() {
        return valueTypes;
    }

    public void registerValueType(ConfigValueType type) {
        valueTypes.add(type);
    }

    public void unregisterValueType(ConfigValueType type) {
        valueTypes.remove(type);
    }

    private Map<String, ConfigValueType> cachedUnique = new HashMap<>();
    public ConfigValueType getType(String id) {
        for (ConfigValueType type : valueTypes) {
            if (type.getId().equals(id)) {
                return type;
            }
        }
        ConfigValueType unique = cachedUnique.get(id);
        if (unique != null) {
            return unique;
        }
        for (Bundle bundle : NodeFlow.getApplication().getBundleManager().getLoadedBundles()) {
            if (bundle instanceof JavaBundle) {
                Class<?> found = ((JavaBundle) bundle).findClass(id);
                if (found != null) {
                    ObjectValueType type = new ObjectValueType(found, I18n.direct(found.getName()));
                    cachedUnique.put(id, type);
                    return type;
                }
            }
        }
        return defaultType;
    }
}
