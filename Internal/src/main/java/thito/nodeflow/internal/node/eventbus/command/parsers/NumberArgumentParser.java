package thito.nodeflow.internal.node.eventbus.command.parsers;

import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.node.eventbus.command.*;
import thito.nodeflow.api.project.*;
import thito.reflectedbytecode.*;
import thito.reflectedbytecode.jvm.*;

public class NumberArgumentParser implements ArgumentParser {
    @Override
    public String getId() {
        return PARSER_NUMBER;
    }

    @Override
    public I18nItem getDisplayName() {
        return I18n.$("parser-number");
    }

    @Override
    public ArgumentParserHandler createHandler() {
        return new ArgumentParserHandler() {
            @Override
            public ArgumentParser getParser() {
                return NumberArgumentParser.this;
            }

            @Override
            public Reference parseArgument(Reference sender, Reference argument) {
                ILocalField field = Code.getCode().getLocalFieldMap().createField(Java.Class(Number.class));
                Java.Try(() -> {
                    field.set(Java.Class(Double.class).getMethod("parseDouble", String.class).get()
                            .invoke(null, argument));
                }).Catch(Throwable.class).Caught(error -> {
                    Java.Try(() -> {
                        field.set(Java.Class(Long.class).getMethod("parseLong", String.class).get()
                                .invoke(null, argument));
                    }).Catch(Throwable.class).Caught(error2 -> {
                        Java.Throw(Java.Class(IllegalArgumentException.class).newInstance());
                    });
                });
                return field.get();
            }

            @Override
            public Reference requestTabCompleter(Reference sender, Reference string) {
                return null;
            }
        };
    }

    @Override
    public Class<?> getType() {
        return Number.class;
    }

    @Override
    public ProjectFacet getFacet() {
        return null;
    }
}
