package thito.nodeflow.internal.node.eventbus.command;

import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.node.eventbus.command.*;
import thito.nodeflow.api.project.*;
import thito.nodeflow.internal.node.provider.*;

import java.util.*;

public class CommandNodeCategoryImpl extends SimpleNodeProviderCategory implements CommandNodeCategory {
    private String id;
    private List<CommandVariable> variables = new ArrayList<>();
    private ProjectFacet facet;
    private CommandHandler handler;
    public CommandNodeCategoryImpl(String id, ProjectFacet facet, CommandHandler handler) {
        super(id, "Command", null);
        this.id = id;
        this.handler = handler;
        this.facet = facet;
    }

    public CommandHandler getHandler() {
        return handler;
    }

    @Override
    public List<NodeProvider> getProviders() {
        List<NodeProvider> providers = new ArrayList<>();
        for (Class<?> type : handler.getSenderTypes()) {
            providers.add(findProvider("nodeflow://command/"+id+"/"+type.getName()+"/"));
        }
        return providers;
    }

    public ProjectFacet getFacet() {
        return facet;
    }

    public List<CommandVariable> getVariables() {
        return variables;
    }

    public String getId() {
        return id;
    }

    @Override
    public NodeProvider findProvider(String id) {
        for (Class<?> type : handler.getSenderTypes()) {
            String prefix = "nodeflow://command/"+this.id+"/"+type.getName()+"/";
            if (id.startsWith(prefix)) {
                String data = id.substring(prefix.length());
                CommandProviderImpl provider = new CommandProviderImpl(id, "On Command", this, type);
                if (!data.isEmpty()) {
                    String[] parsers = data.split(";");
                    for (String parser : parsers) {
                        if (parser.equals("null")) {
                            provider.getParsers().add(null);
                            continue;
                        }
                        ArgumentParser p = CommandManagerImpl.getInstance().deserialize(parser);
                        provider.getParsers().add(p);
                    }
                }
                provider.getVariables().addAll(getVariables());
                provider.initializeParameters();
                return provider;
            }
        }
        return null;
    }
}
