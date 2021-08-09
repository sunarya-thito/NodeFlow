package thito.nodeflow.internal.editor.config.savemodes;

import thito.nodeflow.api.locale.*;
import thito.nodeflow.internal.editor.config.*;
import thito.reflectedbytecode.*;
import thito.reflectedbytecode.jvm.*;

import java.io.*;
import java.util.*;

public class ObjectFileSaveMode implements ConfigFileSaveMode {
    @Override
    public String getId() {
        return "serialization";
    }

    @Override
    public I18nItem getDisplayName() {
        return I18n.$("save-mode-binary");
    }

    @Override
    public String getExtension() {
        return "bin";
    }

    @Override
    public boolean isSavable(ConfigValueType type) {
        return type.getFieldType().isPrimitive() || Serializable.class.isAssignableFrom(type.getFieldType());
    }

    @Override
    public void handleSaveMethod(ConfigFileCompiler compiler, Reference outputStream, GMethodAccessor accessor, Map<GField, String> fields) {
        Java.Try(() -> {
            ILocalField stream = accessor.putVariable(Java.Class(ObjectOutputStream.class), Java.Class(ObjectOutputStream.class).getConstructor(OutputStream.class).get().newInstance(outputStream));
            ILocalField map = accessor.putVariable(Java.Class(Map.class), Java.Class(HashMap.class).getConstructor(int.class).get().newInstance(fields.size()));
            fields.forEach((field, name) -> {
                map.get().method("put", Object.class, Object.class)
                        .invokeVoid(field.getName(), field.get(null));
            });
            stream.get().method("writeObject", Object.class).invokeVoid(map.get());
            stream.get().method("close").invokeVoid();
        }).Catch(Throwable.class).Caught(error -> {
            error.printStackTrace();
        });
    }

    @Override
    public void handleLoadMethod(ConfigFileCompiler compiler, Reference inputStream, GMethodAccessor accessor, Map<GField, String> fields) {
        Java.Try(() -> {
            ILocalField stream = accessor.putVariable(Java.Class(ObjectInputStream.class), Java.Class(ObjectInputStream.class).getConstructor(InputStream.class).get().newInstance(inputStream));
            Reference map = Java.Cast(stream.get().method("readObject").invoke(), Map.class);
            fields.forEach((field, name) -> {
                Reference result = map.method("get", Object.class).invoke(field.getName());
                field.set(null, result);
            });
            inputStream.method("close").invokeVoid();
        }).Catch(Throwable.class).Caught(error -> {
            error.printStackTrace();
        });
    }
}
