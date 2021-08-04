package thito.nodeflow.internal.node.provider.java;

import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.internal.node.*;
import thito.nodeflow.internal.node.provider.*;
import thito.reflectedbytecode.jvm.*;

public class TryCatchBlock extends AbstractNodeProvider {

    private Class<?> type;
    public TryCatchBlock(Class<?> type, NodeProviderCategory category) {
        super("java#tryCatchBlock://"+type.getName(), "Try Catch", category);
        this.type = type;
        addParameter(new ExecutionNodeParameter("Execution", LinkMode.MULTIPLE, LinkMode.SINGLE));
        addParameter(new ExecutionNodeParameter("Test", LinkMode.NONE, LinkMode.SINGLE));
        addParameter(new ExecutionNodeParameter("On Error", LinkMode.NONE, LinkMode.SINGLE));
        addParameter(new JavaNodeParameter("Error", type, true, LinkMode.NONE, LinkMode.MULTIPLE));
    }

    @Override
    public CompileSession createNewSession() {
        return new CompileSession() {
            @Override
            protected void prepareStructure() {
                getHandler().computeField(getNode().getParameter(3));
                getHandler().prepareStructure(findOutputNode(getNode().getParameter(0)));
                getHandler().prepareStructure(findOutputNode(getNode().getParameter(1)));
                getHandler().prepareStructure(findOutputNode(getNode().getParameter(2)));
            }

            @Override
            protected void handleCompile() {
                Java.Try(() -> {
                    getHandler().compile(findOutputNode(getNode().getParameter(1)));
                }).Catch(type).Caught(error -> {
                    getHandler().setReference(getNode().getParameter(3), error);
                    getHandler().compile(findOutputNode(getNode().getParameter(2)));
                });
                getHandler().compile(findOutputNode(getNode().getParameter(0)));
            }
        };
    }

    @Override
    public NodeCompileSession createCompileSession(Node node) {
        return new NodeCompileSession() {
            @Override
            public void handleCompile(NodeCompileHandler handler) {
                Java.Try(() -> {
                    handler.compile(findOutputNode(node.getParameter(1)));
                }).Catch(type).Caught(error -> {
                    handler.putReference(node.getParameter(3), error);
                    handler.compile(findOutputNode(node.getParameter(2)));
                });
                handler.compile(findOutputNode(node.getParameter(0)));
            }
        };
    }

}
