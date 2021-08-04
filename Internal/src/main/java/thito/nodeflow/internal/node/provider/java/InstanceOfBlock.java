package thito.nodeflow.internal.node.provider.java;

import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.internal.node.*;
import thito.nodeflow.internal.node.provider.*;
import thito.reflectedbytecode.jvm.*;

public class InstanceOfBlock extends AbstractNodeProvider {
    private Class<?> type;
    public InstanceOfBlock(Class<?> type, NodeProviderCategory category) {
        super("java#instanceof://"+type.getName(), "Instance Of "+type.getSimpleName(), category);
        this.type = type;
        addParameter(new JavaNodeParameter("Object", Object.class, true, LinkMode.SINGLE, LinkMode.NONE));
        addParameter(new JavaNodeParameter("Result", boolean.class, true, LinkMode.NONE, LinkMode.MULTIPLE));
    }

    @Override
    public CompileSession createNewSession() {
        return new CompileSession() {
            @Override
            protected void prepareStructure() {
                getHandler().prepareLocalField(this, getNode().getParameter(0));
                getHandler().computeField(getNode().getParameter(1));
            }

            @Override
            protected void handleCompile() {
                getHandler().setReference(getNode().getParameter(1), Java.InstanceOf(
                        getHandler().getReference((getNode().getParameter(0))),
                        type
                ));
            }
        };
    }

    @Override
    public NodeCompileSession createCompileSession(Node node) {
        return new NodeCompileSession() {
            @Override
            public void handleCompile(NodeCompileHandler handler) {
                if (isParameterUsed(node.getParameter(1))) {
                    handler.putReference(node.getParameter(1), Java.InstanceOf(
                            handler.getReference((node.getParameter(0))),
                            type
                    ));
                }
            }
        };
    }

}
