package thito.nodeflow.internal.node;

import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.node.*;
import thito.nodeflow.api.node.eventbus.*;
import thito.nodeflow.api.node.eventbus.command.*;
import thito.nodeflow.internal.node.eventbus.*;
import thito.nodeflow.internal.node.eventbus.command.*;
import thito.nodeflow.internal.node.headless.*;
import thito.nodeflow.internal.node.provider.*;
import thito.reflectedbytecode.*;
import thito.reflectedbytecode.jvm.*;

import java.lang.reflect.*;
import java.util.*;

public class NodeCompiler {
    private Context context;
    private ProjectCompiler parent;
    private NodeModule module;

    private GClass clazz;

    private Map<Node, CompileSession> sessionMap = new HashMap<>();

    public NodeCompiler(Context context, ProjectCompiler parent, NodeModule module) {
        this.context = context;
        this.parent = parent;
        this.module = module;
        beginCompile();
    }

    public Map<Node, CompileSession> getSessionMap() {
        return sessionMap;
    }

    public NodeModule getModule() {
        return module;
    }

    public ProjectCompiler getParent() {
        return parent;
    }

    private void beginCompile() {
        clazz = context.createClass(getParent().getPackageName()+".N_"+getParent().requestClassId());
        for (Node node : module.getNodes()) {
            CompileSession session = ((AbstractNodeProvider) node.getState().getProvider()).createNewSession();
            session.node = node;
            sessionMap.put(node, session);
        }
        findEvents();
        findCommands();
    }

    private void findEvents() {
        Set<Node> nodes = module.getNodes();
        for (Node node  : nodes) {
            if (node.getState().getProvider() instanceof EventProvider) {
                CompileHandler handler = new CompileHandler(clazz, this);
                compileEvent((EventProvider) node.getState().getProvider(), node, handler);
            }
        }
    }

    private void findCommands() {
        Set<Node> nodes = module.getNodes();
        for (Node node : nodes) {
            if (node.getState().getProvider() instanceof CommandProviderImpl) {
                CompileHandler handler = new CompileHandler(clazz, this);
                compileCommand((CommandProviderImpl) node.getState().getProvider(), node, handler);
            }
        }
    }

    private void compileCommand(CommandProviderImpl provider, Node node, CompileHandler handler) {
        GMethod method = handler.getGeneratedClass().declareMethod("N_"+getParent().requestMethodId());
        method.modifier().makeStatic();
        method.returnType(boolean.class);
        handler.prepareStructure(node);
        List<Type> types = new ArrayList<>();
        types.add(provider.getSenderType());
        types.add(String.class); // Command name
        types.add(String[].class); // Command arguments
        types.add(boolean.class);
        types.add(boolean.class);
        for (CommandVariable variable : provider.getVariables()) {
            types.add(variable.getType());
        }
        method.parameters(types.toArray(new Type[0]));
        method.body(body -> {
            handler.setAccessor(body);
            handler.beginCompile(node);
        });
        GMethod tab = handler.getGeneratedClass().declareMethod("N_"+getParent().requestMethodId());
        tab.modifier().makeStatic();
        tab.returnType(List.class);
        tab.parameters(provider.getSenderType(), String.class, String[].class);
        tab.body(body -> {
            for (int i = 0; i < provider.getParsers().size(); i++) {
                ArgumentParser parser = provider.getParsers().get(i);
                int index = i;
                Java.If(body.getArgument(2).arrayLength()).isEqualsTo(i + 1).Then(() -> {
                    ILocalField list = body.createVariable(Java.Class(ArrayList.class));
                    list.set(Java.Class(ArrayList.class).newInstance());
                    if (parser == null) {
                        List<String> constants = provider.getConstants(node, index);
                        for (String s : constants) {
                            list.get().method("add", Object.class).invokeVoid(s);
                        }
                    } else {
                        Reference result = parser.createHandler().requestTabCompleter(body.getArgument(0), body.getArgument(2).arrayGet(index));
                        list.get().method("addAll", Collection.class).invokeVoid(result);
                    }
                    body.doneReturn(list.get());
                }).EndIf();
            }
            body.doneReturn(null);
        });
        CompiledCommandNodeImpl compiled = new CompiledCommandNodeImpl(provider.getSenderType(), node, method, tab);
        parent.getHandler().getCompiledCommands().add(compiled);
    }

    private void compileEvent(EventProvider provider, Node node, CompileHandler handler) {
        GMethod method = handler.getGeneratedClass().declareMethod("N_"+getParent().requestMethodId());
        method.modifier().makeStatic();
        List<Type> types = new ArrayList<>();
        for (EventParameter parameter : provider.getEventParameters()) {
            if (parameter != null) types.add(parameter.getType());
        }
        method.parameters(types.toArray(new Type[0]));
        handler.prepareStructure(node);
        method.body(body -> {
            handler.setAccessor(body);
            handler.beginCompile(node);
        });
        CompiledEvent event = new CompiledEventImpl(method, (HeadlessEventNode) node, provider);
        parent.getHandler().getCompiledEvents().add(event);
    }

}
