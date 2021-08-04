package thito.nodeflow.internal.node;

import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.chat.*;
import thito.nodeflow.api.*;
import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.internal.node.parameter.*;
import thito.nodeflow.internal.node.provider.*;
import thito.nodeflow.minecraft.chat.*;
import thito.reflectedbytecode.*;
import thito.reflectedbytecode.jvm.*;

import java.lang.reflect.*;
import java.util.*;

public class CompileHandler {

    private CompileHandler parentHandler;

    private GClass clazz;

    private GConstructor constructor;

    private List<NodeParameter> parameters = new ArrayList<>();

    private Map<UUID, GField> syntheticFields = new HashMap<>();

    private Map<UUID, Object> referenceMap = new HashMap<>();

    private Map<UUID, LField> reservedLocalFields = new LinkedHashMap<>();

    private Set<UUID> skippedLocal = new HashSet<>();

    private Set<Node> compiled = new HashSet<>();

    private int localIndex = 0;

    private int fieldIndex = 0;

    private NodeCompiler nodeCompiler;
    private MemberBodyAccessor accessor;

    public CompileHandler(CompileHandler parentHandler, GClass clazz, NodeCompiler compiler) {
        this.nodeCompiler = compiler;
        this.parentHandler = parentHandler;
        this.clazz = clazz;
    }

    public CompileHandler(GClass clazz, NodeCompiler nodeCompiler) {
        this.clazz = clazz;
        this.nodeCompiler = nodeCompiler;
    }

    public MemberBodyAccessor getAccessor() {
        return accessor;
    }

    public void setAccessor(MemberBodyAccessor accessor) {
        this.accessor = accessor;
    }

    public String toString() {
        if (parentHandler != null) {
            return parentHandler + " > "+clazz.getSimpleName();
        } else {
            return clazz.getSimpleName();
        }
    }

    public void bindScope(CompileHandler handler) {
        syntheticFields = handler.syntheticFields;
        parameters = handler.parameters;
    }

    public NodeCompiler getNodeCompiler() {
        return nodeCompiler;
    }

    public GClass getGeneratedClass() {
        return clazz;
    }

    public void skipLocal(NodeParameter parameter) {
        skippedLocal.add(parameter.getID());
    }

    public boolean prepareLocalField(CompileSession session, NodeParameter parameter) {
        if (parameter == null) return true; // returns true to stop the lookup
        NodeParameter inputLink = parameter.getInputLink();
        if (inputLink == null) return true;
        CompileSession inputSession = getNodeCompiler().getSessionMap().get(inputLink.getNode());
        return prepareLocalField_(inputSession, inputLink);
    }

    private boolean prepareLocalField_(CompileSession session, NodeParameter parameter) {
        if (parameter == null ||
                skippedLocal.contains(parameter.getID()) ||
                syntheticFields.containsKey(parameter.getID()) ||
                referenceMap.containsKey(parameter.getID())) return true;
        if (session.handler == null) {
            prepareStructure(parameter.getNode());
            return true;
        }
        LField local = reservedLocalFields.get(parameter.getID());
        if (local == null) { // local never be null
            if (parentHandler == null || !requestFromParent(session, parameter)) {
                prepareStructure(parameter.getNode());
            }
        }
        return true;
    }

    public Type getDeclaredType(NodeParameter parameter) {
        AbstractNodeProvider abstractNodeProvider = (AbstractNodeProvider) parameter.getNode().getState().getProvider();
        return abstractNodeProvider.getParameters().get(parameter.getNode().getParameters().indexOf(parameter)).getType();
    }

    public Reference createInstance(CompileHandler handler, Object[] parameters) {
        ArrayList<Object> params = new ArrayList<>();
        for (NodeParameter param : this.parameters) {
            params.add(handler.getSavedReference(param.getID()));
        }
        params.addAll(Arrays.asList(parameters));
        return constructor.newInstance(params.toArray());
    }

    public ILocalField createLocalField(Type type) {
        return Code.getCode().getLocalFieldMap().createField(Java.Class(type));
    }

    public ILocalField createLocalField(Type type, Reference reference) {
        ILocalField field = createLocalField(type);
        field.set(reference);
        return field;
    }

    public boolean requestFromParent(CompileSession session, NodeParameter parameter) {
        // make the parent prepare for the local field required by this handler
        // if the parent handler doesn't have the requested parameter
        // it will do the same thing as this handler, i.e requesting from the parent's parent
        // or ignored and given null otherwise
        if (session.handler == parentHandler && parentHandler.prepareLocalField_(session, parameter)) {
            // add NodeParameter to the constructor argument indicating that this handler needs the parameter taken from the upper/outer class
            if (!parameters.contains(parameter)) {
                parameters.add(parameter);
            }
            // synthetic fields for temporary value storage
            if (!syntheticFields.containsKey(parameter.getID())) {
                GField field = clazz.declareField("f_"+fieldIndex++, getDeclaredType(parameter)).modifier().makePrivate().done();
                syntheticFields.put(parameter.getID(), field);
            }
            return true;
        }
        return false;
        // this will return true if the parent has the parameter value stored
        // otherwise, it will keep looking up into its parent
    }

    public LField computeField(NodeParameter parameter) {
        if (reservedLocalFields.containsKey(parameter.getID())) throw new IllegalStateException("already computed: "+parameter.getNode().getState().getProviderID()+" > "+parameter.getNode().getParameters().indexOf(parameter));
        LField field = new LField(Java.Class(getDeclaredType(parameter)), localIndex++);
        reservedLocalFields.put(parameter.getID(), field);
        referenceMap.put(parameter.getID(), field.get());
        return field;
    }

    public Object getSavedReference(UUID uuid) {
        Object reference = referenceMap.get(uuid);
        if (reference != null) {
            return reference;
        }
        return Java.Null();
    }

    public Object getReference(NodeParameter parameter) {
        if (parameter == null) return Java.Null();
        NodeParameter inputLink = parameter.getInputLink();
        if (inputLink == null) {
            Object value = parameter.getValue();
            AbstractNodeProvider abs = (AbstractNodeProvider) parameter.getNode().getState().getProvider();
            NodeParameterFactory f = abs.getParameters().get(parameter.getNode().getParameters().indexOf(parameter));
            if (NodeFlow.getApplication().getBundleManager().findClass(BaseComponent[].class.getName()).isAssignableFrom(f.getType()) && value instanceof String) {
                value = ComponentCompiler.compile(ComponentSerializer.parse((String) value));
            }
            if (value != null) {
                return Reference.javaToReference(value);
            }
            return Java.Null();
        }

        GField synthetic = syntheticFields.get(inputLink.getID());
        if (synthetic != null) {
            return synthetic.get(Reference.getSelf(clazz));
        }

        CompileSession session = getNodeCompiler().getSessionMap().get(inputLink.getNode());
        if (session.handler == this) {
            compile(inputLink.getNode());
        }

        Object reference = referenceMap.get(inputLink.getID());
        if (reference != null) {
            return reference;
        }

        return Java.Null();
    }

    public void setDirectReference(NodeParameter parameter, Object reference) {
        referenceMap.put(parameter.getID(), reference);
    }

    public void setReference(NodeParameter parameter, Object reference) {
        LField local = reservedLocalFields.get(parameter.getID());
        if (local == null) throw new IllegalStateException("unprepared parameter: "+parameter+" from "+parameter.getNode().getState().getProvider().getID());
        local.setType(Java.Class(ASMHelper.GetType(reference)));
        local.set(reference);
    }

    public void prepareStructure(Node node) {
        if (node != null) {
            CompileSession session = getNodeCompiler().getSessionMap().get(node);
            if (session.handler == null) {
                // if the session isn't defined in any handler, then define it here
                session.handler = this;
                // define the session here:
                session.prepareStructure();
            }
        }
    }

    public void compile(Node node) {
        if (node == null) return;
        CompileSession session = getNodeCompiler().getSessionMap().get(node);
        if (session.handler == this) { // only compile nodes that are defined in this handler
            if (compiled.add(node)) {
                session.compile(accessor);
            }
        } else throw new IllegalStateException("Invalid scope for "+node.getState().getProviderID()+" from "+session.handler+" tried to access from "+this);
    }

    public void beginStructure(Node node) {
        prepareStructure(node);
    }

    public void beginConstructor(Node node, Constructor<?> superCons) {
        if (parentHandler != null) {
            constructor = clazz.declareConstructor();
            ArrayList<Type> types = new ArrayList<>();
            for (int i = 0; i < parameters.size(); i++) {
                NodeParameter parameter = parameters.get(i);
                types.add(getDeclaredType(parameter));
            }
            if (superCons != null) {
                types.addAll(Arrays.asList(superCons.getParameterTypes()));
            }
            constructor.parameters(types.toArray(new Type[0]));
            constructor.body(body -> {
                if (superCons != null) {
                    Object[] superReferences = new Object[superCons.getParameterCount()];
                    for (int i = 0; i < superReferences.length; i++) {
                        superReferences[i] = body.getArgument(i + parameters.size() + 1);
                    }
                    body.construct(new KConstructor(superCons), superReferences);
                } else {
                    body.constructDefault();
                }
                for (int i = 0; i < parameters.size(); i++) {
                    NodeParameter param = parameters.get(i);
                    GField synthetic = syntheticFields.get(param.getID()); // theres no way this is null
                    synthetic.set(body, body.getArgument(i + 1));
                }
            });
        }
    }

    public void beginCompile(Node root) {
        for (Map.Entry<UUID, LField> local : reservedLocalFields.entrySet()) {
            ArrayList<ILocalField> fields = Code.getCode().getLocalFieldMap().getPreserved();
            int index = fields.size();
            fields.add(local.getValue());
            local.getValue().setLocalIndex(index);
        }
        compile(root);
    }
}
