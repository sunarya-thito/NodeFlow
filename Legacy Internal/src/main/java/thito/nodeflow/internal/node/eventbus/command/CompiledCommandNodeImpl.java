package thito.nodeflow.internal.node.eventbus.command;

import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.node.eventbus.command.*;
import thito.reflectedbytecode.*;

import java.util.*;
import java.util.stream.*;

public class CompiledCommandNodeImpl implements CompiledCommandNode {
    private Class<?> type;
    private Node node;
    private GMethod method;
    private GMethod tab;

    public CompiledCommandNodeImpl(Class<?> type, Node node, GMethod method, GMethod tab) {
        this.type = type;
        this.node = node;
        this.method = method;
        this.tab = tab;
    }

    @Override
    public Reference requestTabComplete(Reference sender, Reference command, Reference arguments) {
        return tab.invoke(null, sender, command, arguments);
    }

    @Override
    public String[][] getArguments() {
        CommandNodeProvider provider = (CommandNodeProvider) node.getState().getProvider();
        List<String[]> arguments = new ArrayList<>();
        for (int i = 3 + provider.getVariables().size(), index = 0; i < node.getState().getParameters().length - 1; i++, index++) {
            if (((CommandProviderImpl) provider).isConstant(index)) {
                arguments.add(parse(node.getState().getParameters()[i].getConstantValue()));
            } else {
                arguments.add(null);
            }
        }
        NodeParameter last = node.getParameter(node.getParameters().size() - 1);
        if (last.hasOutputLink()) {
            arguments.add(parse(last.getState().getConstantValue()));
        }
        return arguments.toArray(new String[0][0]);
    }

    static String[] parse(Object o) {
        if (o instanceof String) {
            return new String[] { (String) o };
        }
        if (o instanceof List) {
            return ((List<?>) o).stream().map(String::valueOf).toArray(String[]::new);
        }
        return new String[0];
    }

    @Override
    public String getUsage() {
        CommandNodeProvider provider = (CommandNodeProvider) node.getState().getProvider();
        List<String> arguments = new ArrayList<>();
        for (int i = 3 + provider.getVariables().size(); i < node.getState().getParameters().length - 1; i++) {
            arguments.add("<"+String.join("|", parse(node.getState().getParameters()[i].getConstantValue()))+">");
        }
        NodeParameter last = node.getParameter(node.getParameters().size() - 1);
        if (last.hasOutputLink()) {
            arguments.add("<"+String.join("|", parse(last.getState().getConstantValue()))+">");
        }
        return String.join(" ", arguments);
    }

    @Override
    public List<String> getCommandName() {
        CommandNodeProvider provider = (CommandNodeProvider) node.getState().getProvider();
        Object value = node.getState().getParameters()[2 + provider.getVariables().size()].getConstantValue();
        if (value instanceof List) {
            return ((List<?>) value).stream().map(String::valueOf).collect(Collectors.toList());
        } else if (value instanceof String) {
            return Collections.singletonList((String) value);
        }
        return Collections.emptyList();
    }

    @Override
    public String getPermissionMessage() {
        return node.getState().getExtras().getString("command-permission-message");
    }

    @Override
    public String getInvalidArgumentMessage() {
        return node.getState().getExtras().getString("command-invalid-argument-message");
    }

    @Override
    public String getNotFoundMessage() {
        return node.getState().getExtras().getString("command-not-found-message");
    }

    @Override
    public String getInvalidSenderMessage() {
        return node.getState().getExtras().getString("command-invalid-sender-message");
    }

    @Override
    public String getPermission() {
        return node.getState().getExtras().getString("command-permission");
    }

    @Override
    public String getDescription() {
        return node.getState().getExtras().getString("command-description");
    }

    @Override
    public Class<?> getSenderType() {
        return type;
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    public Reference invoke(Reference sender, Reference command, Reference arguments, boolean ignoreError, boolean execute) {
        return method.invoke(null, sender, command, arguments, ignoreError, execute);
    }
}
