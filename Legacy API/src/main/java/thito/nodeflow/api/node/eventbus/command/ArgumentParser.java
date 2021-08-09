package thito.nodeflow.api.node.eventbus.command;

import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.project.*;
import thito.reflectedbytecode.*;

public interface ArgumentParser {
    String
    PARSER_STRING = "string",
    PARSER_BOOLEAN = "boolean",
    PARSER_NUMBER = "number",
    PARSER_CHARACTER = "character"
    ;

    String getId();
    I18nItem getDisplayName();
    Class<?> getType();
    ArgumentParserHandler createHandler();
    ProjectFacet getFacet();
}
