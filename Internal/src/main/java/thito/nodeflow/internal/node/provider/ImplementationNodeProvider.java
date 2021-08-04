package thito.nodeflow.internal.node.provider;

import thito.nodeflow.api.editor.node.Node;
import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.internal.node.*;
import thito.nodeflow.library.*;
import thito.reflectedbytecode.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

public class ImplementationNodeProvider extends AbstractNodeProvider {
    private Class<?> type;
    private Constructor<?> constructor;
    public ImplementationNodeProvider(Class<?> type, Constructor<?> constructor, NodeProviderCategory category) {
        super("java://implementation#"+type.getName()+"<init>"+(constructor == null ? "()" : ConstructorNodeProvider.toStringParameters(constructor.getParameterTypes())), "Implementation of "+type.getSimpleName(), category);
        this.type = type;
        addParameter(new ExecutionNodeParameter("Execution", LinkMode.MULTIPLE, LinkMode.SINGLE));
        this.constructor = constructor;
        if (constructor != null) {
            Optional<List<String>> names = ParameterNames.getParameterNamesFromBytecode(constructor);
            Parameter[] parameters = constructor.getParameters();
            for (int i = 0; i < parameters.length; i++) {
                Parameter param = parameters[i];
                String name = names.isPresent() ? names.get().get(i) : param.getName();
                name = JavaNodeProviderCategory.capitalizeCamelCase(name);
                addParameter(new JavaNodeParameter(name, param.getParameterizedType(), false, LinkMode.SINGLE, LinkMode.NONE));
            }
        }
        addParameter(new MethodOverrideNodeParameter(type, null, false));
        addParameter(new JavaNodeParameter("Instance", type, true, LinkMode.NONE, LinkMode.MULTIPLE));
    }

    public Constructor<?> getConstructor() {
        return constructor;
    }

    @Override
    public CompileSession createNewSession() {
        return new CompileSession() {
            private CompileHandler child;
            private Map<NodeParameter, CompileHandler> implementations = new HashMap<>();
            @Override
            protected void prepareStructure() {
                if (constructor != null) {
                    for (int i = 0; i < constructor.getParameterCount(); i++) {
                        getHandler().prepareLocalField(this, getNode().getParameter(i + 1));
                    }
                }
                getHandler().computeField(getNode().getParameter(getNode().getParameters().size() - 1));
                getHandler().prepareStructure(findOutputNode(getNode().getParameter(0)));

                GClass inner = getHandler()
                        .getGeneratedClass()
                        .getContext()
                        .createClass(getHandler().getNodeCompiler().getParent().getPackageName()+".N_"+getHandler().getNodeCompiler().getParent().requestClassId());
                if (type.isInterface()) {
                    inner.thatImplements(type);
                } else {
                    inner.thatExtends(type);
                }
                child = new CompileHandler(getHandler(), inner, getHandler().getNodeCompiler());

                for (NodeParameter output : getNode().getParameter(getNode().getParameters().size() - 2).collectOutputLinks()) {
                    CompileHandler out = new CompileHandler(getHandler(), child.getGeneratedClass(), child.getNodeCompiler());
                    out.setDirectReference(getNode().getParameter(getNode().getParameters().size() - 1), Reference.getSelf(inner));
                    out.bindScope(child);
                    out.beginStructure(output.getNode());
                    MethodNodeProvider provider = (MethodNodeProvider) output.getNode().getState().getProvider();
                    Method method = provider.getMethod();
                    GMethod innerMethod = inner.declareMethod(method.getName())
                            .modifier().makePublic().done()
                            .returnType(method.getReturnType())
                            .parameters(method.getParameterTypes());
                    innerMethod.body(body -> {
                        out.setAccessor(body);
                        out.beginCompile(output.getNode());
                    });
                    implementations.put(output, out);
                }

                child.beginConstructor(getNode(), constructor);
            }

            @Override
            protected void handleCompile() {
                Object[] arguments = constructor == null ? new Object[0] : new Object[constructor.getParameterCount()];
                for (int i = 0; i < arguments.length; i++) {
                    arguments[i] = getHandler().getReference(getNode().getParameter(i + 1));
                }

                getHandler().setReference(getNode().getParameter(getNode().getParameters().size() - 1),
                        child.createInstance(getHandler(), arguments));

                getHandler().compile(findOutputNode(getNode().getParameter(0)));
            }
        };
    }

    @Override
    public NodeCompileSession createCompileSession(Node node) {
        return new NodeCompileSession() {
            @Override
            public void handleCompile(NodeCompileHandler handler) {
                List<Object> arguments = new ArrayList<>();
                arguments.add(Reference.getSelf(handler.getCompiledClass()));
                GClass inner = handler.getCompiledClass().declareInnerClass();
                inner.modifier().makePublic().makeStatic().makeFinal();
                if (type.isInterface()) {
                    inner.thatImplements(type);
                } else {
                    inner.thatExtends(type);
                }
                ArrayList<Type> parameterTypes = new ArrayList<>();
                parameterTypes.add(handler.getCompiledClass());
                if (constructor != null) {
                    parameterTypes.addAll(Arrays.asList(constructor.getParameterTypes()));
                    for (int i = 0; i < constructor.getParameterCount(); i++) {
                        arguments.add(handler.getReference(node.getParameter(i + 1)));
                    }
                }
                GConstructor constructor = inner.declareConstructor().modifier().makePublic().done()
                        .parameters(parameterTypes.toArray(new Type[0]));
                if (isParameterUsed(node.getParameter(node.getParameters().size() - 1))) {
                    handler.putReference(node.getParameter(node.getParameters().size() - 1),
                            inner.getConstructors()[0].newInstance(
                                    arguments.toArray()
                            ));
                } else {
                    inner.getConstructors()[0].newInstanceVoid(
                            arguments.toArray()
                    );
                }
                GField parent = inner.declareField("parent", handler.getCompiledClass()).modifier().makePublic().done();
                constructor.body(body -> {
                    Constructor<?> Super = ImplementationNodeProvider.this.constructor;
                    if (Super != null) {
                        body.construct(new KConstructor(Super), IntStream.range(0, Super.getParameterCount())
                        .mapToObj(x -> body.getArgument(2 + x)).toArray());
                    } else {
                        body.constructDefault();
                    }
                    parent.set(body, body.getArgument(1));
                });
                for (NodeParameter x : findOutputLinks(node.getParameter(node.getParameters().size() - 2))) {
                    Node nx = x.getNode();
                    if (nx.getState().getProvider() instanceof MethodNodeProvider &&
                        ((MethodNodeProvider) nx.getState().getProvider()).isImplementation()) {
                        Method method = ((MethodNodeProvider) nx.getState().getProvider()).getMethod();
                        GMethod innerMethod = inner.declareMethod(method.getName())
                                .modifier().makePublic().done()
                                .returnType(method.getReturnType())
                                .parameters(method.getParameterTypes());
                        NodeCompileHandlerImpl innerHandler = new NodeCompileHandlerImpl(((NodeCompileHandlerImpl) handler).getCompiler(), nx);
                        Reference self = Reference.getSelf(inner);
                        innerHandler.inheritParent((NodeCompileHandlerImpl) handler, parent);
                        innerHandler.putInheritance(node.getParameter(node.getParameters().size() - 1), self);
                        innerHandler.beginCompile(inner, innerMethod);
                    }
                }
                handler.compile(findOutputNode(node.getParameter(0)));
            }
        };
    }

}
