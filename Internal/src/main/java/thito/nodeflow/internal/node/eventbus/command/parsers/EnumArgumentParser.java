package thito.nodeflow.internal.node.eventbus.command.parsers;

import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.node.eventbus.command.*;
import thito.nodeflow.api.project.*;
import thito.reflectedbytecode.*;
import thito.reflectedbytecode.jvm.*;

import java.lang.reflect.*;
import java.util.*;

public class EnumArgumentParser implements ArgumentParser {
    private Class<?> enumClass;

    public EnumArgumentParser(Class<?> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public String getId() {
        return "enum:"+enumClass.getName();
    }

    @Override
    public I18nItem getDisplayName() {
        return I18n.direct("Enum "+enumClass.getSimpleName());
    }

    @Override
    public Class<?> getType() {
        return enumClass;
    }

    @Override
    public ArgumentParserHandler createHandler() {
        return new ArgumentParserHandler() {
            @Override
            public ArgumentParser getParser() {
                return EnumArgumentParser.this;
            }

            @Override
            public Reference parseArgument(Reference sender, Reference string) {
                ILocalField field = Code.getCode().getLocalFieldMap().createField(Java.Class(enumClass));
                Java.Try(() -> {
                    field.set(Java.Class(enumClass).staticMethod("valueOf", String.class).invoke(string));
                }).Catch(Throwable.class).Caught(error -> {
                    Java.Throw(Java.Class(NullPointerException.class).newInstance());
                });
                return field.get();
            }

            @Override
            public Reference requestTabCompleter(Reference sender, Reference string) {
                ILocalField field = Code.getCode().getLocalFieldMap().createField(Java.Class(List.class));
                field.set(Java.Class(ArrayList.class).newInstance());
                ILocalField array = Code.getCode().getLocalFieldMap().createField(Java.Class(ASMHelper.ConvertToArrayType(enumClass, 1)));
                array.set(Java.Class(enumClass).staticMethod("values").invoke());
                ILocalField index = Code.getCode().getLocalFieldMap().createField(Java.Class(int.class));
                index.set(0);
                ILocalField element = Code.getCode().getLocalFieldMap().createField(Java.Class(enumClass));
                Java.Loop(aWhile -> {
                    Java.If(index.get()).isLessThan(array.get().arrayLength()).Then(() -> {
                        element.set(array.get().arrayGet(index.get()));
                        field.get().method("add", Object.class).invokeVoid(element.get().method("name").invoke());
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
