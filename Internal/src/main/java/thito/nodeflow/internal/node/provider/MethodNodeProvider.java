package thito.nodeflow.internal.node.provider;

import javafx.beans.*;
import org.objectweb.asm.*;
import thito.nodeflow.api.editor.node.Node;
import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.node.*;
import thito.nodeflow.api.node.state.*;
import thito.nodeflow.internal.node.*;
import thito.nodeflow.internal.node.state.*;
import thito.nodeflow.library.*;
import thito.nodejfx.parameter.*;
import thito.reflectedbytecode.*;
import thito.reflectedbytecode.jvm.*;

import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

public class MethodNodeProvider extends AbstractNodeProvider implements ClassMemberProvider {
    private Method method;

    private int varargsTotal;
    private String varargsName;
//    private int varargsAddIndex = -1;
    private Parameter varargsType;
    private boolean implementation;

    private Map<Integer, Integer> parameterMap = new HashMap<>();

    public MethodNodeProvider(Method method, int varargsTotal, NodeProviderCategory category, boolean implementation) {
        super("method("+varargsTotal+" "+implementation+"):"+method.getDeclaringClass().getName()+"#"+method.getName()+ ConstructorNodeProvider.toStringParameters(method.getParameterTypes())+method.getReturnType().getName(), JavaNodeProviderCategory.capitalizeCamelCase(method.getName()), category);
        this.method = method;
        this.varargsTotal = varargsTotal;
        this.implementation = implementation;
        if (implementation) {
            addParameter(new MethodOverrideNodeParameter(method.getDeclaringClass(), method, true));
            addParameter(new ExecutionNodeParameter("Execution", LinkMode.NONE, LinkMode.SINGLE));
        } else {
            addParameter(new ExecutionNodeParameter("Execution", LinkMode.MULTIPLE, LinkMode.SINGLE));
        }
        if (!Modifier.isStatic(method.getModifiers()) && !implementation) {
            addParameter(new JavaNodeParameter("Target", new ParameterizedTypeImpl(method.getDeclaringClass()), false, LinkMode.SINGLE, LinkMode.NONE));
        }
        Parameter[] parameters = method.getParameters();
        Optional<List<String>> names = ParameterNames.getParameterNamesFromBytecode(method);
        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            String name = names.isPresent() ? names.get().get(i) : param.getName();
            name = JavaNodeProviderCategory.capitalizeCamelCase(name);
            if (param.getType().isArray() && i == parameters.length - 1 && varargsTotal >= 0 && !implementation) {
                if (getParameters().size() > 0) {
                    NodeParameterFactory lastParam = getParameters().get(getParameters().size() - 1);
                    if (lastParam instanceof JavaNodeParameter) {
                        ((JavaNodeParameter) lastParam).insertable();
                    } else if (lastParam instanceof ExecutionNodeParameter) {
                        ((ExecutionNodeParameter) lastParam).insertable();
                    }
                }
                varargsType = param;
                varargsName = name;
                Type component = param.getType().getComponentType();
                if (param.getParameterizedType() instanceof GenericArrayType) {
                    component = ((GenericArrayType) param.getParameterizedType()).getGenericComponentType();
                }
                for (int j = 0; j < varargsTotal; j++) {
                    JavaNodeParameter parameter;
                    parameterMap.put(getParameters().size(), i);
                    addParameter(parameter = new JavaNodeParameter(name, component, false, LinkMode.SINGLE, LinkMode.NONE));
                    parameter.insertable();
                    parameter.removable();
                }
            } else {
                parameterMap.put(getParameters().size(), i);
                if (implementation) {
                    addParameter(new JavaNodeParameter(name, param.getParameterizedType(), false, LinkMode.NONE, LinkMode.MULTIPLE));
                } else {
                    addParameter(new JavaNodeParameter(name, param.getParameterizedType(), false, LinkMode.SINGLE, LinkMode.NONE));
                }
            }
        }
        if (!method.getReturnType().equals(void.class)) {
            if (implementation) {
                addParameter(new JavaNodeParameter("Result", method.getGenericReturnType(), true, LinkMode.SINGLE, LinkMode.NONE));
            } else {
                addParameter(new JavaNodeParameter("Result", method.getGenericReturnType(), true, LinkMode.NONE, LinkMode.MULTIPLE));
            }
        }
    }

    public Map<Integer, Integer> getParameterMap() {
        return parameterMap;
    }

    @Override
    public CompileSession createNewSession() {
        return new CompileSession() {
            @Override
            protected void prepareStructure() {
                int index = 0;
                if (implementation) {
                    index += 2;
                } else {
                    index++;
                }
                if (!Modifier.isStatic(method.getModifiers()) && !implementation) {
                    getHandler().prepareLocalField(this, getNode().getParameter(index++));
                }
                Parameter[] parameters = method.getParameters();
                for (int i = 0; i < parameters.length; i++) {
                    Parameter param = parameters[i];
                    if (param.getType().isArray() && i == parameters.length - 1 && varargsTotal >= 0 && !implementation) {
                        for (int j = 0; j < varargsTotal; j++) {
                            getHandler().prepareLocalField(this, getNode().getParameter(index++));
                        }
                    } else {
                        if (implementation) {
                            getHandler().skipLocal(getNode().getParameter(index++));
                        } else {
                            getHandler().prepareLocalField(this, getNode().getParameter(index++));
                        }
                    }
                }
                if (!method.getReturnType().equals(void.class)) {
                    if (implementation) {
                        getHandler().prepareLocalField(this, getNode().getParameter(index++));
                    } else {
                        getHandler().computeField(getNode().getParameter(index++));
                    }
                }
                if (implementation) {
                    getHandler().prepareStructure(findOutputNode(getNode().getParameter(1)));
                } else {
                    getHandler().prepareStructure(findOutputNode(getNode().getParameter(0)));
                }
            }

            @Override
            protected void handleCompile() {
                if (implementation) {
                    Parameter[] parameters = method.getParameters();
                    for (int i = 0; i < parameters.length; i++) {
                        Parameter param = parameters[i];
                        int finalI = i;
                        getHandler().setDirectReference(getNode().getParameter(2 + i), new Reference(param.getType()) {
                            @Override
                            protected void write() {
                                Code.getCode().getCodeVisitor().visitVarInsn(
                                        ASMHelper.ToASMType(param.getType()).getOpcode(Opcodes.ILOAD), finalI + 1
                                );
                            }
                        });
                    }
                    getHandler().compile(findOutputNode(getNode().getParameter(1)));
                    if (!void.class.equals(method.getReturnType())) {
                        Return(getHandler().getReference(getNode().getParameter(getNode().getParameters().size() - 1)));
                    }
                } else {
                    boolean staticMethod = Modifier.isStatic(method.getModifiers());
                    Parameter[] parameters = method.getParameters();
                    Object[] args = new Object[parameters.length];
                    for (int i = 0; i < parameters.length; i++) {
                        Parameter param = parameters[i];
                        if (param.getType().isArray() && i == parameters.length - 1 && varargsTotal >= 0) {
                            int finalI = i;
                            args[i] = Java.NewArray(param.getType().getComponentType(), varargsTotal)
                                    .arrayInitialValues(IntStream.range(0, varargsTotal).mapToObj(x -> {
                                        NodeParameter parameter = getNode().getParameter((staticMethod ? 1 : 2) + finalI + x);
                                        return getHandler().getReference(parameter);
                                    }).toArray());
                        } else {
                            NodeParameter parameter = getNode().getParameter((staticMethod ? 1 : 2) + i);
                            args[i] = getHandler().getReference(parameter);
                        }
                    }
                    if (!void.class.equals(method.getReturnType())) {
                        getHandler().setReference(getNode().getParameter(getNode().getParameters().size() - 1),
                                new KMethod(method).invoke(staticMethod ? null : getHandler().getReference((getNode().getParameter(1))),
                                        args));
                    } else {
                        new KMethod(method)
                                .invokeVoid(
                                        staticMethod ?
                                                null :
                                                getHandler().getReference((getNode().getParameter(1))),
                                        args);
                    }
                    getHandler().compile(findOutputNode(getNode().getParameter(0)));
                }
            }
        };
    }

    @Override
    public Member getMember() {
        return method;
    }

    @Override
    public String toString() {
        return "MethodNodeProvider{" +
                "method=" + method +
                '}';
    }

    public boolean isImplementation() {
        return implementation;
    }

    public boolean hasVarArgs() {
        return varargsType != null;
    }

    public Method getMethod() {
        return method;
    }

    @Override
    public Node fromState(NodeModule module, ComponentState state) {
        Node node = super.fromState(module, state);
        if (isImplementation()) {
            ((NodeImpl) node).getStorage().setBounds(true);
        }
        if (node instanceof NodeImpl) {
            for (NodeParameter parameter : node.getParameters()) {
                thito.nodejfx.NodeParameter param = ((NodeParameterImpl) parameter).impl_getPeer();
                param.getAddButton().setOnMouseClicked(event -> {
                    JavaNodeParameter javaParam = new JavaNodeParameter(varargsName,
                            varargsType.getParameterizedType() instanceof Class ?
                                    ((Class<?>) varargsType.getParameterizedType()).getComponentType() :
                                    ((GenericArrayType) varargsType.getParameterizedType()).getGenericComponentType(), false, LinkMode.SINGLE, LinkMode.NONE);
                    javaParam.removable();
                    javaParam.insertable();
                    NodeParameter nodeParameter = javaParam.createParameter(node, new ComponentParameterStateImpl((StandardNodeModule) module, UUID.randomUUID(), null));
                    int index = node.getParameters().indexOf(parameter) + 1;
                    node.getParameters().add(index, nodeParameter);
                });
            }
            if (hasVarArgs()) {
                ((NodeImpl) node).getParameters().addListener((InvalidationListener) observable -> {
                    int additional = method.getParameterCount() - 1;
                    if (Modifier.isPublic(method.getModifiers())) {
                        additional++;
                    }
                    if (!method.getReturnType().equals(void.class)) {
                        additional++;
                    }
                    for (NodeParameter parameter : node.getParameters()) {
                        thito.nodejfx.NodeParameter param = ((NodeParameterImpl) parameter).impl_getPeer();
                        param.getAddButton().setOnMouseClicked(event -> {
                            JavaNodeParameter javaParam = new JavaNodeParameter(varargsName,
                                    varargsType.getParameterizedType() instanceof Class ?
                                            ((Class<?>) varargsType.getParameterizedType()).getComponentType() :
                                            ((GenericArrayType) varargsType.getParameterizedType()).getGenericComponentType(), false, LinkMode.SINGLE, LinkMode.NONE);
                            javaParam.removable();
                            javaParam.insertable();
                            NodeParameter nodeParameter = javaParam.createParameter(node, new ComponentParameterStateImpl((StandardNodeModule) module, UUID.randomUUID(), null));
                            int index = node.getParameters().indexOf(parameter) + 1;
                            node.getParameters().add(index, nodeParameter);
                        });
                    }
                    ((MethodNodeProvider) node.getState().getProvider()).varargsTotal = node.getParameters().size() - additional;
                    int totalVar = ((MethodNodeProvider) node.getState().getProvider()).varargsTotal;
                    node.getState().setProviderID("method("+totalVar+"):"+method.getDeclaringClass().getName()+"#"+method.getName()+ ConstructorNodeProvider.toStringParameters(method.getParameterTypes())+method.getReturnType().getName());
                    ((StandardNodeModule) module).attemptSave();
                });
            }
        }
        return node;
    }

    @Override
    public NodeCompileSession createCompileSession(Node node) {
        return new NodeCompileSession() {
            @Override
            public void handleCompile(NodeCompileHandler handler) {
                if (implementation) {
                    Parameter[] parameters = method.getParameters();
                    for (int i = 0; i < parameters.length; i++) {
                        Parameter param = parameters[i];
                        if (param.getType().isArray() && i == parameters.length - 1 && varargsTotal >= 0) {
                            for (int j = 0; j < varargsTotal; j++) {
                                handler.putReference(node.getParameter(2 + i + j), handler.getParameterReference(i).arrayGet(j));
                            }
                        } else {
                            handler.putReference(node.getParameter(2 + i), handler.getParameterReference(i));
                        }
                    }
                    handler.compile(findOutputNode(node.getParameter(1)));
                    if (!void.class.equals(method.getReturnType())) {
                        handler.returnValue(handler.getReference((node.getParameter(node.getParameters().size() - 1))));
                    }
                } else {
                    boolean staticMethod = Modifier.isStatic(method.getModifiers());
                    Parameter[] parameters = method.getParameters();
                    Object[] args = new Object[parameters.length];
                    for (int i = 0; i < parameters.length; i++) {
                        Parameter param = parameters[i];
                        if (param.getType().isArray() && i == parameters.length - 1 && varargsTotal >= 0) {
                            int finalI = i;
                            args[i] = Java.NewArray(param.getType().getComponentType(), varargsTotal)
                                    .arrayInitialValues(IntStream.range(0, varargsTotal).mapToObj(x -> handler.getReference((node.getParameter((staticMethod ? 1 : 2) + finalI + x)))));
                        } else {
                            args[i] = handler.getReference((node.getParameter((staticMethod ? 1 : 2) + i)));
                        }
                    }
                    if (!void.class.equals(method.getReturnType())) {
                        handler.putReference(node.getParameter(node.getParameters().size() - 1),
                                new KMethod(method).invoke(staticMethod ? null : handler.getReference((node.getParameter(1))),
                                        args));
                    } else {
                        new KMethod(method)
                                .invoke(
                                        staticMethod ?
                                                null :
                                                handler.getReference((node.getParameter(1))),
                                        args);
                    }
                    handler.compile(findOutputNode(node.getParameter(0)));
                }
            }
        };
    }

}
