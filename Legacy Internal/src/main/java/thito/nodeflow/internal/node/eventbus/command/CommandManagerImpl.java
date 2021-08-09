package thito.nodeflow.internal.node.eventbus.command;

import thito.nodeflow.api.*;
import thito.nodeflow.api.config.*;
import thito.nodeflow.api.node.eventbus.command.*;
import thito.nodeflow.api.project.*;
import thito.nodeflow.internal.bundle.*;
import thito.nodeflow.internal.node.*;
import thito.nodeflow.internal.node.eventbus.command.parsers.*;

import java.util.*;

public class CommandManagerImpl implements CommandManager {

    private static final CommandManagerImpl instance = new CommandManagerImpl();

    public static CommandManagerImpl getInstance() {
        return instance;
    }

    private Set<ArgumentParser> parsers = new HashSet<>();

    public Set<ArgumentParser> getParsers() {
        return parsers;
    }

    public CommandManagerImpl() {
        getParsers().add(new StringArgumentParser());
        getParsers().add(new BooleanArgumentParser());
        getParsers().add(new NumberArgumentParser());
        getParsers().add(new CharacterArgumentParser());
    }

    @Override
    public CommandNodeCategory createCategory(String id, ProjectFacet facet, CommandHandler handler) {
        CommandNodeCategory category = new CommandNodeCategoryImpl(id, facet, handler);
        ModuleManagerImpl.getInstance().registerCategory(category);
        return category;
    }

    public ArgumentParser deserialize(String type) {
        if (type.startsWith("enum:")) {
            Class<?> clazz = NodeFlow.getApplication().getBundleManager().findClass(type.substring(5));
            if (clazz == null) {
                type = ArgumentParser.PARSER_STRING;
            } else {
                return new EnumArgumentParser(clazz);
            }
        }
        if (type.startsWith("constant:")) {
            Class<?> clazz = NodeFlow.getApplication().getBundleManager().findClass(type.substring(9));
            if (clazz == null) {
                type = ArgumentParser.PARSER_STRING;
            } else {
                return new EnumArgumentParser(clazz);
            }
        }
        String finalType = type;
        return parsers.stream().filter(x -> x.getId().equals(finalType)).findFirst().get();
    }

    public Section serialize(ArgumentParser parser) {
        Section section = Section.newMap();
        section.set(parser.getId(), "type");
        return section;
    }

    @Override
    public void registerParser(ArgumentParser parser) {
        parsers.add(parser);
    }

    @Override
    public void unregisterParser(ArgumentParser parser) {
        parsers.add(parser);
    }
}
