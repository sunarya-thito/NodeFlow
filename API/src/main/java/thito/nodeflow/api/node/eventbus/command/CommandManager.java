package thito.nodeflow.api.node.eventbus.command;

import thito.nodeflow.api.project.*;

public interface CommandManager {

    CommandNodeCategory createCategory(String id, ProjectFacet facet, CommandHandler handler);
    void registerParser(ArgumentParser parser);
    void unregisterParser(ArgumentParser parser);

}
