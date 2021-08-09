package thito.nodeflow.api.node.eventbus.command;

import thito.nodeflow.api.editor.node.*;
import thito.reflectedbytecode.*;

import java.util.*;

public interface CommandNodeProvider {
    List<CommandVariable> getVariables();
}
