package thito.nodeflow.internal.node;

import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.node.eventbus.*;
import thito.nodeflow.internal.node.provider.*;
import thito.reflectedbytecode.*;
import thito.reflectedbytecode.jvm.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

public class NodeCompileHandlerImpl implements NodeCompileHandler {
    private GMethodAccessor body;
    private NodeCompileHandlerImpl parent;
    private GField parentField;
    private Map<UUID, Reference> overridden = new ConcurrentHashMap<>();
    private Map<UUID, GField> savedReference = new ConcurrentHashMap<>();
    private Set<UUID> noCleanUp = new HashSet<>();
    private boolean cleanUpParent = true;
    private Map<Node, NodeCompileSession> savedSession = new ConcurrentHashMap<>(); // to make sure that theres no null key
    private Node node;
    private GClass clazz;
    private NodeProvider provider;
    private NodeCompiler compiler;
    private int id;
    private GMethod method;

    public NodeCompileHandlerImpl(NodeCompiler compiler, Node node) {
        NodeProvider provider = node.getState().getProvider();
        this.node = node;
        this.provider = provider;
        this.compiler = compiler;
    }

    public void removeInheritance(NodeParameter parameter) {
        savedReference.remove(parameter.getID());
    }

    public void putInheritance(NodeParameter parameter, Reference reference) {
        overridden.put(parameter.getID(), reference);
    }

    public void preventCleanUp(NodeParameter parameter) {
        if (parent != null) {
            parent.preventCleanUp(parameter);
        }
        noCleanUp.add(parameter.getID());
    }

    public static Reference lookup(NodeCompileHandlerImpl handler, NodeParameter parameter, Reference instance) {
        if (handler == null) return null;
        GField field = handler.savedReference.get(parameter.getID());
        if (field == null) {
            if (handler.parentField != null) {
                handler.cleanUpParent = false;
                return lookup(handler.parent, parameter, handler.parentField.get(instance));
            }
            return null;
        }
        return field.get(instance);
    }

    public void inheritParent(NodeCompileHandlerImpl parent, GField parentField) {
        this.parent = parent;
        this.parentField = parentField;
    }

    public NodeCompiler getCompiler() {
        return compiler;
    }

    public GMethodAccessor getBody() {
        return body;
    }

    @Override
    public ILocalField createLocalVariable(IClass type) {
        return body.createVariable(type);
    }

    @Override
    public GClass getCompiledClass() {
        return clazz;
    }

    public void beginCompile() {
        clazz = compiler.getParent().getContext().createClass(compiler.getParent().getPackageName()+".modules.Node_"+compiler.getParent().requestClassId());
        method = clazz.declareMethod("run");
        method.modifier().makePublic();
        method.parameters(((EventProvider) provider).getEventParameters().stream().filter(Objects::nonNull).map(x -> x.getType()).toArray(Type[]::new));
        method.body(body -> {
            this.body = body;
            getCompileSession(node).compile(this, node);
            doCleanUp();
        });
        clazz.declareMethod("cleanup").body(body -> {
            if (parentField != null && cleanUpParent) {
                parentField.set(Reference.getSelf(clazz), Java.Null());
            }
            for (Map.Entry<UUID, GField> field : savedReference.entrySet()) {
                if (!noCleanUp.contains(field.getKey())) {
                    field.getValue().set(body, Java.Null());
                }
            }
        });
    }

    public void doCleanUp() {
        Reference.getSelf(clazz).method("cleanup").invoke();
    }

    public void beginCompile(GClass clazz, GMethod method) {
        this.clazz = clazz;
        this.method = method;
        method.body(body -> {
            this.body = body;
            getCompileSession(node).compile(this, node);
            doCleanUp();
        });
        clazz.declareMethod("cleanup").body(body -> {
            if (parentField != null && cleanUpParent) {
                parentField.set(Reference.getSelf(clazz), Java.Null());
            }
            for (Map.Entry<UUID, GField> field : savedReference.entrySet()) {
                if (!noCleanUp.contains(field.getKey())) {
                    field.getValue().set(body, Java.Null());
                }
            }
        });
    }

    public GMethod getMethod() {
        return method;
    }

    @Override
    public void returnValue(Object object) {
        body.doneReturn(object);
    }

    @Override
    public Reference getParameterReference(int index) {
        return body.getArgument(index);
    }

    public void beginInnerCompile(GMethod method, Consumer<GMethodAccessor> precompile) {
        clazz = compiler.getParent().getContext().createClass(compiler.getParent().getPackageName()+".modules.Node_"+compiler.getParent().requestClassId());
        method.body(body -> {
            this.body = body;
            precompile.accept(body);
            getCompileSession(node).compile(this, node);
        });
    }

    @Override
    public void compile(Node node) {
        if (node != null) {
            NodeCompileSession session = getCompileSession(node);
            session.compile(this, node);
        }
    }

    @Override
    public NodeCompileSession getCompileSession(Node node) {
        return savedSession.computeIfAbsent(node, key -> key.getState()
                .getProvider()
                .createCompileSession(key));
    }

    @Override
    public Reference getReference(NodeParameter input) {
        Object constantValue = input.getValue();
        if (constantValue != null) {
            NodeParameterFactory factory = ((AbstractNodeProvider) input.getNode().getState().getProvider()).getParameters().get(input.getNode().getParameters().indexOf(input));
            if (factory.getType() != null && factory.getType().isEnum()) {
                return Java.Enum(factory.getType()).valueOf(String.valueOf(constantValue));
            }
            if (constantValue instanceof Field) {
                constantValue = Java.Enum(((Field) constantValue).getType()).valueOf(((Field) constantValue).getName());
            }
            return Reference.javaToReference(constantValue);
        }
        input = input.getInputLink();
        if (input == null) {
            return Java.Null();
        }
        Reference overridden = this.overridden.get(input.getID());
        if (overridden != null) {
            return overridden;
        }
        if (this.parent != null) {
            Reference parent = lookup(this.parent, input, parentField.get(Reference.getSelf(clazz)));
            if (parent != null) {
                putReference(input, parent);
            }
        }
        if (!savedReference.containsKey(input.getID())) {
            compile(input.getNode());
        }
        return savedReference.computeIfAbsent(input.getID(), id -> clazz.declareField("f_"+compiler.getParent().requestMethodId(), Object.class)
        .modifier().makePublic().done()).get(Reference.getSelf(clazz));
    }

    private static Type getType(NodeParameter parameter) {
        Object type = parameter.impl_getType();
        if (type instanceof JavaNodeParameter) {
            return ((JavaNodeParameter) type).getType();
        }
        return Object.class;
    }

    @Override
    public void putReference(NodeParameter parameter, Object reference) {
        savedReference.computeIfAbsent(parameter.getID(), id -> clazz.declareField("f_"+compiler.getParent().requestMethodId(),
                ASMHelper.GetType(reference))
                .modifier().makePublic().done())
        .set(Reference.getSelf(clazz), reference);
    }

    @Override
    public Set<NodeParameter> getRequiredParameters(Node node) {
        Set<NodeParameter> parameters = new HashSet<>();
        Set<Node> nodes = new HashSet<>();

        return parameters;
    }

}
