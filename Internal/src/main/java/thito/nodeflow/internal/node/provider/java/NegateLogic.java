package thito.nodeflow.internal.node.provider.java;

import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.internal.node.*;
import thito.nodeflow.internal.node.provider.*;
import thito.reflectedbytecode.jvm.*;

public class NegateLogic extends AbstractNodeProvider {

    public NegateLogic(NodeProviderCategory category) {
        super("java#negate", "Negate Boolean", category);
        addParameter(new JavaNodeParameter("Input", boolean.class, false, LinkMode.SINGLE, LinkMode.NONE));
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
                getHandler().setReference(getNode().getParameter(1), Java.Logic.Negate(getHandler().getReference((getNode().getParameter(0)))));
            }
        };
    }

    @Override
    public NodeCompileSession createCompileSession(Node node) {
        return new NodeCompileSession() {
            @Override
            public void handleCompile(NodeCompileHandler handler) {
                if (isParameterUsed(node.getParameter(1))) {
                    handler.putReference(node.getParameter(1), Java.Logic.Negate(handler.getReference((node.getParameter(0)))));
                }
            }
        };
    }

}
