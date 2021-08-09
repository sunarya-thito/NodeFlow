package thito.nodeflow.internal.editor.config.savemodes;

import thito.nodeflow.api.locale.*;
import thito.nodeflow.internal.editor.config.*;
import thito.reflectedbytecode.*;

import java.util.*;

public class DisabledSaveMode implements ConfigFileSaveMode {
    @Override
    public String getId() {
        return "disabled";
    }

    @Override
    public I18nItem getDisplayName() {
        return I18n.$("save-mode-disabled");
    }

    @Override
    public String getExtension() {
        return null;
    }

    @Override
    public boolean isSavable(ConfigValueType type) {
        return true;
    }

    @Override
    public void handleSaveMethod(ConfigFileCompiler compiler, Reference outputStream, GMethodAccessor accessor, Map<GField, String> fields) {

    }

    @Override
    public void handleLoadMethod(ConfigFileCompiler compiler, Reference inputStream, GMethodAccessor accessor, Map<GField, String> fields) {

    }
}
