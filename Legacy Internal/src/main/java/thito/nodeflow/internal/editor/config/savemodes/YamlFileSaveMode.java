package thito.nodeflow.internal.editor.config.savemodes;

import org.yaml.snakeyaml.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.internal.editor.config.*;
import thito.reflectedbytecode.*;
import thito.reflectedbytecode.jvm.*;

import java.io.*;
import java.util.*;

public class YamlFileSaveMode implements ConfigFileSaveMode {
    @Override
    public String getId() {
        return "yaml";
    }

    @Override
    public I18nItem getDisplayName() {
        return I18n.$("save-mode-yaml");
    }

    @Override
    public String getExtension() {
        return "yml";
    }

    @Override
    public boolean isSavable(ConfigValueType type) {
        Class<?> fieldType = type.getFieldType();
        return fieldType == String.class || Number.class.isAssignableFrom(fieldType) || fieldType.isPrimitive() ||
                fieldType == Boolean.class || fieldType == Character.class;
    }

    @Override
    public void handleSaveMethod(ConfigFileCompiler compiler, Reference outputStream, GMethodAccessor accessor, Map<GField, String> fields) {
        Java.Try(() -> {
            ILocalField writer = accessor.putVariable(Java.Class(Writer.class), Java.Class(OutputStreamWriter.class).getConstructor(OutputStream.class).get().newInstance(outputStream));
            ILocalField map = accessor.putVariable(Java.Class(Map.class), Java.Class(HashMap.class).getConstructor(int.class).get().newInstance(fields.size()));
            fields.forEach((field, name) -> {
                map.get().method("put", Object.class, Object.class)
                        .invokeVoid(name, field.get(null));
            });
            ILocalField options = accessor.putVariable(Java.Class(DumperOptions.class), Java.Class(DumperOptions.class).newInstance());
            options.get().method("setPrettyFlow", boolean.class).invokeVoid(true);
            options.get().method("setDefaultFlowStyle", DumperOptions.FlowStyle.class).invokeVoid(Java.Enum(DumperOptions.FlowStyle.class).valueOf("BLOCK"));
            ILocalField yaml = accessor.putVariable(Java.Class(Yaml.class), Java.Class(Yaml.class).getConstructor(DumperOptions.class).get().newInstance(options.get()));
            yaml.get().method("dump", Object.class, Writer.class).invokeVoid(map.get(), writer.get());
            writer.get().method("close").invokeVoid();
        }).Catch(Throwable.class).Caught(error -> {
            error.printStackTrace();
        });
    }

    @Override
    public void handleLoadMethod(ConfigFileCompiler compiler, Reference inputStream, GMethodAccessor accessor, Map<GField, String> fields) {
        Java.Try(() -> {
            ILocalField yaml = accessor.putVariable(Java.Class(Yaml.class), Java.Class(Yaml.class).newInstance());
            Reference map = Java.Cast(yaml.get().method("loadAs", InputStream.class, Class.class).invoke(inputStream, Java.Type(Map.class)), Map.class);
            fields.forEach((field, name) -> {
                Reference result = map.method("get", Object.class).invoke(name);
                field.set(null, result);
            });
            inputStream.method("close").invoke();
        }).Catch(Throwable.class).Caught(error -> {
            error.printStackTrace();
        });
    }
}
