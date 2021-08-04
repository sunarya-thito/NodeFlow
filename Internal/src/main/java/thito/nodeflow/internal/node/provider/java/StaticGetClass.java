package thito.nodeflow.internal.node.provider.java;

import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.internal.node.*;
import thito.nodeflow.internal.node.provider.*;
import thito.reflectedbytecode.jvm.*;

public class StaticGetClass extends AbstractNodeProvider {
    private Class<?> type;
    public StaticGetClass(Class<?> type, NodeProviderCategory category) {
        super("java#class://"+type.getName(), "Get Class", category);
        this.type = type;
        addParameter(new JavaNodeParameter("Class", type.getClass(), false, LinkMode.NONE, LinkMode.MULTIPLE));
    }

    @Override
    public CompileSession createNewSession() {
        return new CompileSession() {
            @Override
            protected void prepareStructure() {
                getHandler().computeField(getNode().getParameter(0));
            }

            @Override
            protected void handleCompile() {
                getHandler().setReference(getNode().getParameter(0), Java.Type(type));
            }
        };
    }

    @Override
    public NodeCompileSession createCompileSession(Node node) {
        return new NodeCompileSession() {
            @Override
            public void handleCompile(NodeCompileHandler handler) {
                if (isParameterUsed(node.getParameter(0))) {
                    handler.putReference(node.getParameter(0),
                            Java.Type(type));
                }
            }
        };
    }

}
