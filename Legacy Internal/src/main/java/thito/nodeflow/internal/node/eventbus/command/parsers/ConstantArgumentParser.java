package thito.nodeflow.internal.node.eventbus.command.parsers;

import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.node.eventbus.command.*;
import thito.nodeflow.api.project.*;
import thito.reflectedbytecode.*;
import thito.reflectedbytecode.jvm.*;

import java.lang.reflect.*;
import java.util.*;

public class ConstantArgumentParser implements ArgumentParser {
    private Class<?> type;

    public ConstantArgumentParser(Class<?> type) {
        this.type = type;
    }

    @Override
    public String getId() {
        return "constant:"+type.getName();
    }

    @Override
    public I18nItem getDisplayName() {
        return I18n.direct("Constant "+type.getSimpleName());
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public ArgumentParserHandler createHandler() {
        return new ArgumentParserHandler() {
            @Override
            public ArgumentParser getParser() {
                return ConstantArgumentParser.this;
            }

            @Override
            public Reference parseArgument(Reference sender, Reference string) {
                ILocalField field = Code.getCode().getLocalFieldMap().createField(Java.Class(Field.class));
                ILocalField result = Code.getCode().getLocalFieldMap().createField(Java.Class(type));
                Java.Try(() -> {
                    field.set(Java.Type(type).method("getField", String.class).invoke(string));
                }).Catch(Throwable.class).Caught(error -> {
                    Java.Throw(Java.Class(NullPointerException.class).newInstance());
                });
                Java.If(Java.Class(Modifier.class).staticMethod("isStatic", int.class).invoke(field.get().method("getModifiers"))).isFalse().Then(() -> {
                    Java.Throw(Java.Class(NullPointerException.class).newInstance());
                }).EndIf();
                Java.If(Java.Class(Modifier.class).staticMethod("isPublic", int.class).invoke(field.get().method("getModifiers"))).isFalse().Then(() -> {
                    Java.Throw(Java.Class(NullPointerException.class).newInstance());
                }).EndIf();
                Java.Try(() -> {
                    result.set(field.get().method("get", Object.class).invoke(null));
                }).Catch(Throwable.class).Caught(error -> {
                    Java.Throw(Java.Class(NullPointerException.class).newInstance());
                });
                return result.get();
            }

            @Override
            public Reference requestTabCompleter(Reference sender, Reference string) {
                ILocalField field = Code.getCode().getLocalFieldMap().createField(Java.Class(List.class));
                field.set(Java.Class(ArrayList.class).newInstance());
                ILocalField array = Code.getCode().getLocalFieldMap().createField(Java.Class(Field[].class));
                array.set(Java.Type(type).method("getDeclaredFields").invoke());
                ILocalField index = Code.getCode().getLocalFieldMap().createField(Java.Class(int.class));
                index.set(0);
                ILocalField element = Code.getCode().getLocalFieldMap().createField(Java.Class(Field.class));
                Java.Loop(aWhile -> {
                    Java.If(index.get()).isLessThan(array.get().arrayLength()).Then(() -> {
                        element.set(array.get().arrayGet(index.get()));
                        field.get().method("add", Object.class).invokeVoid(element.get().method("getName").invoke());
                        index.set(index.get().mathAdd(1));
                    }).Else(() -> {
                        aWhile.Break();
                    }).EndIf();
                });
                return field.get();
            }
        };
    }

    @Override
    public ProjectFacet getFacet() {
        return null;
    }
}
