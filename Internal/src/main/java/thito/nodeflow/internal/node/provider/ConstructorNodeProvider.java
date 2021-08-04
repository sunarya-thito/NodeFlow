package thito.nodeflow.internal.node.provider;

import javafx.beans.*;
import thito.nodeflow.api.editor.node.Node;
import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.node.*;
import thito.nodeflow.api.node.state.*;
import thito.nodeflow.internal.node.*;
import thito.nodeflow.internal.node.state.*;
import thito.nodeflow.library.*;
import thito.nodejfx.parameter.*;
import thito.reflectedbytecode.*;
import thito.reflectedbytecode.jvm.*;

import java.lang.reflect.Member;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

public class ConstructorNodeProvider extends AbstractNodeProvider implements ClassMemberProvider {

    public static String toStringParameters(Class<?>[] params) {
        return "("+String.join(",", Arrays.stream(params).map(Class::getName).collect(Collectors.toList()))+")";
    }

    private Constructor<?> constructor;

    private String varargsName;
    private int varargsTotal;
//    private int varargsAddIndex = -1;
    private Parameter varargsType;

    public ConstructorNodeProvider(Constructor<?> constructor, int varargsTotal, NodeProviderCategory category) {
        super ("constructor("+varargsTotal+"):"+constructor.getDeclaringClass().getName()+"#<init>"+toStringParameters(constructor.getParameterTypes()), "Create "+ModuleManagerImpl.toStringSimple(constructor.getDeclaringClass()), category);
        this.constructor = constructor;
        addParameter(new ExecutionNodeParameter("Execution", LinkMode.MULTIPLE, LinkMode.SINGLE));
        Optional<List<String>> names = ParameterNames.getParameterNamesFromBytecode(constructor);
        Parameter[] parameters = constructor.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            String name = names.isPresent() ? names.get().get(i) : param.getName();
            name = JavaNodeProviderCategory.capitalizeCamelCase(name);
            if (param.getType().isArray() && i == parameters.length - 1 && varargsTotal >= 0) {
                if (getParameters().size() > 0) {
                    NodeParameterFactory lastParam = getParameters().get(getParameters().size() - 1);
                    if (lastParam instanceof JavaNodeParameter) {
                        ((JavaNodeParameter) lastParam).insertable();
                    } else if (lastParam instanceof ExecutionNodeParameter) {
                        ((ExecutionNodeParameter) lastParam).insertable();
                    }
                }
                varargsName = name;
                varargsType = param;
                Class<?> component = param.getType().getComponentType();
                for (int j = 0; j < varargsTotal; j++) {
                    JavaNodeParameter parameter;
                    addParameter(parameter = new JavaNodeParameter(name, component, false, LinkMode.SINGLE, LinkMode.NONE));
                    parameter.insertable();
                    parameter.removable();
                }
            } else {
                addParameter(new JavaNodeParameter(name, param.getParameterizedType(), false, LinkMode.SINGLE, LinkMode.NONE));
            }
        }
        addParameter(new JavaNodeParameter("Result", constructor.getDeclaringClass(), true, LinkMode.NONE, LinkMode.MULTIPLE));
    }

    @Override
    public Member getMember() {
        return constructor;
    }

    public boolean hasVarArgs() {
        return varargsType != null;
    }

    @Override
    public CompileSession createNewSession() {
        return new CompileSession() {
            @Override
            protected void prepareStructure() {
                int index = 0;
                Parameter[] parameters = constructor.getParameters();
                for (int i = 0; i < parameters.length; i++) {
                    Parameter param = parameters[i];
                    if (param.getType().isArray() && i == parameters.length - 1 && varargsTotal >= 0) {
                        for (int j = 0; j < varargsTotal; j++) {
                            getHandler().prepareLocalField(this, getNode().getParameter(index++));
                        }
//                        index++;
                    } else {
                        getHandler().prepareLocalField(this, getNode().getParameter(index++));
                    }
                }
                getHandler().computeField(getNode().getParameter(getNode().getParameters().size() - 1));
                getHandler().prepareStructure(findOutputNode(getNode().getParameter(0)));
            }

            @Override
            protected void handleCompile() {
                KConstructor kConstructor = new KConstructor(constructor);
                Parameter[] parameters = constructor.getParameters();
                Object[] args = new Object[parameters.length];
                for (int i = 0; i < parameters.length; i++) {
                    Parameter param = parameters[i];
                    if (param.getType().isArray() && i == parameters.length - 1 && varargsTotal >= 0) {
                        int finalI = i;
                        args[i] = Java.NewArray(param.getType().getComponentType(), varargsTotal)
                                .arrayInitialValues(IntStream.range(0, varargsTotal).mapToObj(x -> getHandler().getReference(getNode().getParameter(1 + finalI + x).getInputLink())).toArray());
                    } else {
                        args[i] = getHandler().getReference(getNode().getParameter(1 + i));
                    }
                }
                getHandler().setReference(getNode().getParameter(getNode().getParameters().size() - 1), kConstructor.newInstance(args));
                getHandler().compile(getNode().getParameter(0));
            }
        };
    }

    @Override
    public NodeCompileSession createCompileSession(Node node) {
        return new NodeCompileSession() {
            @Override
            public void handleCompile(NodeCompileHandler handler) {
                KConstructor kConstructor = new KConstructor(constructor);
                Parameter[] parameters = constructor.getParameters();
                Object[] args = new Object[parameters.length];
                for (int i = 0; i < parameters.length; i++) {
                    Parameter param = parameters[i];
                    if (param.getType().isArray() && i == parameters.length - 1 && varargsTotal >= 0) {
                        int finalI = i;
                        args[i] = Java.NewArray(param.getType().getComponentType(), varargsTotal)
                                .arrayInitialValues(IntStream.range(0, varargsTotal).mapToObj(x -> handler.getReference((node.getParameter(1 + finalI + x)))));
                    } else {
                        args[i] = handler.getReference((node.getParameter(1 + i)));
                    }
                }
                if (isParameterUsed(node.getParameter(kConstructor.getParameterCount() + 1))) {
                    handler.putReference(node.getParameter(node.getParameters().size() - 1),
                            kConstructor.newInstance(args));
                } else {
                    kConstructor.newInstance(args);
                }
                handler.compile(findOutputNode(node.getParameter(0)));
            }
        };
    }

    public Constructor<?> getConstructor() {
        return constructor;
    }

    @Override
    public Node fromState(NodeModule module, ComponentState state) {
        Node node = super.fromState(module, state);
        //
        if (node instanceof NodeImpl) {
            for (NodeParameter parameter : node.getParameters()) {
                thito.nodejfx.NodeParameter param = ((NodeParameterImpl) parameter).impl_getPeer();
                param.getAddButton().setOnMouseClicked(event -> {
                    JavaNodeParameter javaParam = new JavaNodeParameter(varargsName,
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
                    int additional = constructor.getParameterCount() - 1;
                    if (Modifier.isPublic(constructor.getModifiers())) {
                        additional++;
                    }
                    for (NodeParameter parameter : node.getParameters()) {
                        thito.nodejfx.NodeParameter param = ((NodeParameterImpl) parameter).impl_getPeer();
                        param.getAddButton().setOnMouseClicked(event -> {
                            JavaNodeParameter javaParam = new JavaNodeParameter(varargsName,
                                    ((GenericArrayType) varargsType.getParameterizedType()).getGenericComponentType(), false, LinkMode.SINGLE, LinkMode.NONE);
                            javaParam.removable();
                            javaParam.insertable();
                            NodeParameter nodeParameter = javaParam.createParameter(node, new ComponentParameterStateImpl((StandardNodeModule) module, UUID.randomUUID(), null));
                            int index = node.getParameters().indexOf(parameter) + 1;
                            node.getParameters().add(index, nodeParameter);
                        });
                    }
                    ((ConstructorNodeProvider) node.getState().getProvider()).varargsTotal = node.getParameters().size() - additional;
                    int totalVar = ((ConstructorNodeProvider) node.getState().getProvider()).varargsTotal;
                    node.getState().setProviderID("method("+totalVar+"):"+constructor.getDeclaringClass().getName()+"#<init>"+ ConstructorNodeProvider.toStringParameters(constructor.getParameterTypes()));
                    ((StandardNodeModule) module).attemptSave();
                });
            }
        }
        return node;
    }

}
