package thito.nodeflow.internal.editor.config;

import thito.nodeflow.api.locale.*;
import thito.reflectedbytecode.*;

import java.util.*;

public interface ConfigFileSaveMode {
    String getId();
    String getExtension();
    I18nItem getDisplayName();
    boolean isSavable(ConfigValueType type);
    void handleSaveMethod(ConfigFileCompiler compiler, Reference outputStream, GMethodAccessor accessor, Map<GField, String> fields);
    void handleLoadMethod(ConfigFileCompiler compiler, Reference inputStream, GMethodAccessor accessor, Map<GField, String> fields);
}
