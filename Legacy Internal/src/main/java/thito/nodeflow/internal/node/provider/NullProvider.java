package thito.nodeflow.internal.node.provider;

import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.internal.node.*;
import thito.reflectedbytecode.jvm.*;

public class NullProvider extends AbstractNodeProvider {
    public NullProvider(NodeProviderCategory category) {
        super("java#null", "Null", category);
        addParameter(new JavaNodeParameter("Null Object", Object.class, true, LinkMode.NONE, LinkMode.MULTIPLE));
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
                getHandler().setReference(getNode().getParameter(0), Java.Null());
            }
        };
    }

    @Override
    public NodeCompileSession createCompileSession(Node node) {
        return new NodeCompileSession() {
            @Override
            public void handleCompile(NodeCompileHandler handler) {
                handler.putReference(node.getParameter(0), Java.Null());
            }
        };
    }

}
