package thito.nodeflow.internal.node.eventbus.command.parsers;

import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.node.eventbus.command.*;
import thito.nodeflow.api.project.*;
import thito.reflectedbytecode.*;
import thito.reflectedbytecode.jvm.*;

import java.util.*;

public class BooleanArgumentParser implements ArgumentParser {
    @Override
    public String getId() {
        return PARSER_BOOLEAN;
    }

    @Override
    public I18nItem getDisplayName() {
        return I18n.$("parser-boolean");
    }

    @Override
    public ArgumentParserHandler createHandler() {
        return new ArgumentParserHandler() {
            @Override
            public ArgumentParser getParser() {
                return BooleanArgumentParser.this;
            }

            @Override
            public Reference parseArgument(Reference sender, Reference argument) {
                ILocalField field = Code.getCode().getLocalFieldMap().createField(Java.Class(boolean.class));
                Java.If(Reference.javaToReference("true")).isEqualsTo(argument).Then(() -> {
                    field.set(Java.Logic.True());
                }).Else(() -> {
                    Java.If(Reference.javaToReference("false")).isEqualsTo(argument).Then(() -> {
                        field.set(Java.Logic.False());
                    }).Else(() -> {
                        Java.Throw(Java.Class(IllegalArgumentException.class).newInstance());
                    }).EndIf();
                }).EndIf();
                return field.get();
            }

            @Override
            public Reference requestTabCompleter(Reference sender, Reference string) {
                ILocalField field = Code.getCode().getLocalFieldMap().createField(Java.Class(List.class));
                field.set(Java.Class(ArrayList.class).newInstance());
                field.get().method("add", Object.class).invokeVoid("true");
                field.get().method("add", Object.class).invokeVoid("false");
                return field.get();
            }
        };
    }

    @Override
    public Class<?> getType() {
        return Boolean.class;
    }

    @Override
    public ProjectFacet getFacet() {
        return null;
    }
}
