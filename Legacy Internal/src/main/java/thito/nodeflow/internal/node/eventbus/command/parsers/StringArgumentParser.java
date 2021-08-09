package thito.nodeflow.internal.node.eventbus.command.parsers;

import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.node.eventbus.command.*;
import thito.nodeflow.api.project.*;
import thito.reflectedbytecode.*;

public class StringArgumentParser implements ArgumentParser {
    @Override
    public I18nItem getDisplayName() {
        return I18n.$("parser-string");
    }

    @Override
    public ArgumentParserHandler createHandler() {
        return new ArgumentParserHandler() {
            @Override
            public ArgumentParser getParser() {
                return StringArgumentParser.this;
            }

            @Override
            public Reference parseArgument(Reference sender, Reference argument) {
                return argument;
            }

            @Override
            public Reference requestTabCompleter(Reference sender, Reference string) {
                return null;
            }
        };
    }

    @Override
    public Class<?> getType() {
        return String.class;
    }

    @Override
    public String getId() {
        return PARSER_STRING;
    }

    @Override
    public ProjectFacet getFacet() {
        return null;
    }
}
