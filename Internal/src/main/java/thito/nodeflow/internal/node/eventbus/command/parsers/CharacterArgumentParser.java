package thito.nodeflow.internal.node.eventbus.command.parsers;

import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.node.eventbus.command.*;
import thito.nodeflow.api.project.*;
import thito.reflectedbytecode.*;
import thito.reflectedbytecode.jvm.*;

public class CharacterArgumentParser implements ArgumentParser {
    @Override
    public String getId() {
        return PARSER_CHARACTER;
    }

    @Override
    public I18nItem getDisplayName() {
        return I18n.$("parser-character");
    }

    @Override
    public Class<?> getType() {
        return Character.class;
    }

    @Override
    public ArgumentParserHandler createHandler() {
        return new ArgumentParserHandler() {
            @Override
            public ArgumentParser getParser() {
                return CharacterArgumentParser.this;
            }

            @Override
            public Reference parseArgument(Reference sender, Reference string) {
                ILocalField field = Code.getCode().getLocalFieldMap().createField(Java.Class(Character.class));
                Java.If(string.method("length").invoke()).isGreaterThan(0).Then(() -> {
                    field.set(string.method("charAt", int.class).invoke(0));
                }).Else(() -> {
                    Java.Throw(Java.Class(IllegalArgumentException.class).newInstance());
                }).EndIf();
                return field.get();
            }

            @Override
            public Reference requestTabCompleter(Reference sender, Reference string) {
                return null;
            }
        };
    }

    @Override
    public ProjectFacet getFacet() {
        return null;
    }
}
