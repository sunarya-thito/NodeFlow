package thito.nodeflow.api.node.eventbus.command;

import thito.nodeflow.api.editor.node.*;

import java.util.*;

public interface CommandNode extends Node {
    String getPermission();
    String getDescription();
    String getPermissionMessage();
    String getInvalidArgumentMessage();
    String getNotFoundMessage();
    String getInvalidSenderMessage();
    List<ArgumentParser> getParsers();
}
