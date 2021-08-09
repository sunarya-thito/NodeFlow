package thito.nodeflow.internal.node.provider.java;

import javafx.beans.*;
import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.node.*;
import thito.nodeflow.api.node.state.*;
import thito.nodeflow.internal.node.*;
import thito.nodeflow.internal.node.provider.*;
import thito.nodeflow.internal.node.state.*;
import thito.nodejfx.parameter.*;
import thito.nodejfx.parameter.type.*;
import thito.reflectedbytecode.jvm.*;

import java.util.*;

public class ArrayProvider extends AbstractNodeProvider {
    private Class<?> clazz, array;
    private int dimensions;

    public ArrayProvider(Class<?> clazz, int dimensions, NodeProviderCategory category) {
        super("java#array://" + clazz.getName() + "(" + dimensions + ")", "Create Array of " + ModuleManagerImpl.toStringSimple(clazz), category);
        this.clazz = clazz;
        this.dimensions = dimensions;
        addParameter(new ExecutionNodeParameter("Execution", LinkMode.MULTIPLE, LinkMode.SINGLE));
        for (int i = 0; i < dimensions; i++) {
            JavaNodeParameter x = new JavaNodeParameter("Size (" + (i + 1) + ")", int.class, false, LinkMode.SINGLE, LinkMode.NONE);
            if (i > 0) x.removable();
            addParameter(x);
        }
        AddNodeParameter parameter = new AddNodeParameter();
        addParameter(parameter);
        try {
            addParameter(new JavaNodeParameter("Array", array = ArrayProviderCategory.asArray(clazz, dimensions), true, LinkMode.NONE, LinkMode.MULTIPLE));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CompileSession createNewSession() {
        return new CompileSession() {
            @Override
            protected void prepareStructure() {
                for (int i = 0; i < dimensions; i++) {
                    getHandler().prepareLocalField(this, getNode().getParameter(i + 1));
                }
                getHandler().computeField(getNode().getParameter(dimensions + 2));
                getHandler().prepareStructure(findOutputNode(getNode().getParameter(0)));
            }

            @Override
            protected void handleCompile() {
                Object[] dimensions = new Object[getNode().getParameters().size() - 3];
                int index = 0;
                for (int i = 1; i < getNode().getParameters().size() - 2; i++) {
                    dimensions[index] = getHandler().getReference((getNode().getParameter(i)));
                    index++;
                }
                getHandler().setReference(getNode().getParameter(getNode().getParameters().size() - 1), Java.NewArray(clazz, dimensions));
                getHandler().compile(findOutputNode(getNode().getParameter(0)));
            }
        };
    }

    @Override
    public NodeCompileSession createCompileSession(Node node) {
        return new NodeCompileSession() {
            @Override
            public void handleCompile(NodeCompileHandler handler) {
                Object[] dimensions = new Object[node.getParameters().size() - 3];
                int index = 0;
                for (int i = 1; i < node.getParameters().size() - 2; i++) {
                    dimensions[index] = handler.getReference((node.getParameter(i)));
                    index++;
                }
                if (isParameterUsed(node.getParameter(node.getParameters().size() - 1))) {
                    handler.putReference(node.getParameter(node.getParameters().size() - 1), Java.NewArray(clazz, dimensions));
                }
                handler.compile(findOutputNode(node.getParameter(0)));
            }
        };
    }

    public void setDimensions(int dimensions) {
        this.dimensions = dimensions;
    }

    public Class<?> getComponent() {
        return clazz;
    }

    @Override
    public String getDescription() {
        return clazz.getName();
    }

    @Override
    public Node fromState(NodeModule module, ComponentState state) {
        Node node = super.fromState(module, state);
        if (node instanceof NodeImpl) {
            NodeParameterImpl param = (NodeParameterImpl) node.getParameters().get(dimensions + 1);
            AddParameter add = (AddParameter) param.impl_getPeer();
            NodeParameterImpl typeParam = (NodeParameterImpl) node.getParameters().get(dimensions + 2);
            add.getButton().setOnMouseClicked(event -> {
                int dimensions = ((ArrayProvider) node.getState().getProvider()).dimensions;
                JavaNodeParameter parameter = new JavaNodeParameter("Size ("+
                        (dimensions + 1)
                        +")", Integer.class, false, LinkMode.SINGLE, LinkMode.NONE);
                parameter.removable();
                NodeParameter par = parameter.createParameter(node, new ComponentParameterStateImpl((StandardNodeModule) module, UUID.randomUUID(), 0));
                node.getParameters().add(dimensions + 1, par);
            });
            ((NodeImpl) node).getParameters().addListener((InvalidationListener) observable -> {
                int dimensions = node.getParameters().size() - 3;
                node.getState().setProviderID("java#array://" + clazz.getName() + "(" + dimensions + ")");
                try {
                    typeParam.setType(JavaParameterType.getType(ArrayProviderCategory.asArray(clazz, dimensions)));
                } catch (ClassNotFoundException e) {
                }
                int index = 1;
                for (NodeParameter parameter : node.getParameters()) {
                    if (parameter.impl_getType() instanceof JavaParameterType && ((JavaParameterType<?>) parameter.impl_getType()).getType() == Integer.class) {
                        ((NodeParameterImpl) parameter).setName("Size ("+index+")");
                        index++;
                    }
                }
                ((StandardNodeModule) module).attemptSave();
            });
        }
        return node;
    }
}
