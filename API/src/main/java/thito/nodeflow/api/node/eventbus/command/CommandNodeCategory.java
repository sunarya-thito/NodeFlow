package thito.nodeflow.api.node.eventbus.command;

import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.project.*;

import java.util.*;

public interface CommandNodeCategory extends NodeProviderCategory {
    String getId();
    List<CommandVariable> getVariables();
    ProjectFacet getFacet();
}
