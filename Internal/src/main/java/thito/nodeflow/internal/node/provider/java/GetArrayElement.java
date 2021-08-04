package thito.nodeflow.internal.node.provider.java;

import thito.nodeflow.api.*;
import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.internal.node.*;
import thito.nodeflow.internal.node.provider.*;
import thito.reflectedbytecode.jvm.*;

public class GetArrayElement extends AbstractNodeProvider {

    private Class<?> clazz, array;
    public GetArrayElement(Class<?> clazz, int dimension, NodeProviderCategory category) {
        super("java#get_array://"+clazz.getName()+"("+dimension+")", "Get Array Element", category);
        try {
            addParameter(new JavaNodeParameter("Array", array = ArrayProviderCategory.asArray(clazz, dimension), true, LinkMode.SINGLE, LinkMode.NONE));
        } catch (ClassNotFoundException e) {
            throw new ReportedError(e);
        }
        addParameter(new JavaNodeParameter("Index", int.class, false, LinkMode.SINGLE, LinkMode.NONE));
        addParameter(new JavaNodeParameter("Result", clazz, true, LinkMode.NONE, LinkMode.MULTIPLE));
    }

    @Override
    public CompileSession createNewSession() {
        return new CompileSession() {
            @Override
            protected void prepareStructure() {
                getHandler().prepareLocalField(this, getNode().getParameter(0));
                getHandler().prepareLocalField(this, getNode().getParameter(1));
                getHandler().computeField(getNode().getParameter(2));
            }

            @Override
            protected void handleCompile() {
                getHandler().setReference(getNode().getParameter(2),
                        Java.GetArrayElement(getHandler().getReference(getNode().getParameter(0)),
                                getHandler().getReference(getNode().getParameter(1))));
            }
        };
    }

    @Override
    public NodeCompileSession createCompileSession(Node node) {
        return new NodeCompileSession() {
            @Override
            public void handleCompile(NodeCompileHandler handler) {
                if (isParameterUsed(node.getParameter(2))) {
                    handler.putReference(node.getParameter(2), Java.GetArrayElement(
                            handler.getReference((node.getParameter(0))),
                            handler.getReference((node.getParameter(1)))
                    ));
                }
            }
        };
    }

}
