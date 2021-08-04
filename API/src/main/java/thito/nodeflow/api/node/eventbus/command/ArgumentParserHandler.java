package thito.nodeflow.api.node.eventbus.command;

import thito.nodeflow.api.editor.node.*;
import thito.reflectedbytecode.*;

public abstract class ArgumentParserHandler {

    public abstract Reference parseArgument(Reference sender, Reference string);

    public abstract Reference requestTabCompleter(Reference sender, Reference string);

    public abstract ArgumentParser getParser();
}
