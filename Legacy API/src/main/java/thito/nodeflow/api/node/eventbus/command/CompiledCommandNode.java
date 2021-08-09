package thito.nodeflow.api.node.eventbus.command;

import thito.nodeflow.api.editor.node.*;
import thito.reflectedbytecode.*;

import java.util.*;

public interface CompiledCommandNode {
    String[][] getArguments();
    Class<?> getSenderType();
    Node getNode();
    Reference invoke(Reference sender, Reference command, Reference arguments, boolean ignoreError, boolean executeCommand);
    Reference requestTabComplete(Reference sender, Reference command, Reference arguments);
    String getInvalidArgumentMessage();
    String getPermission();
    String getDescription();
    String getPermissionMessage();
    String getNotFoundMessage();
    String getInvalidSenderMessage();
    List<String> getCommandName();
    String getUsage();
}
