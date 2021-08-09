package thito.nodeflow.internal.node.provider;

import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.internal.node.*;
import thito.reflectedbytecode.jvm.*;

public class WhileProvider extends AbstractNodeProvider {
    public WhileProvider(NodeProviderCategory category) {
        super("java#while", "While Condition is True", category);
        addParameter(new ExecutionNodeParameter("Execution", LinkMode.MULTIPLE, LinkMode.SINGLE));
        addParameter(new JavaNodeParameter("Condition", boolean.class, false, LinkMode.SINGLE, LinkMode.NONE));
        addParameter(new ExecutionNodeParameter("Do", LinkMode.NONE, LinkMode.SINGLE));
    }

    @Override
    public CompileSession createNewSession() {
        return new CompileSession() {
            @Override
            protected void prepareStructure() {
                getHandler().prepareLocalField(this, getNode().getParameter(1));
                getHandler().prepareStructure(findOutputNode(getNode().getParameter(0)));
            }

            @Override
            protected void handleCompile() {
                Java.Loop(aWhile -> {
                    Java.If(getHandler().getReference(getNode().getParameter(1))).isTrue().Then(() -> {
                        getHandler().compile(findOutputNode(getNode().getParameter(2)));
                    }).Else(() -> {
                        aWhile.Break();
                    }).EndIf();
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
                Java.Loop(aWhile -> {
                    Java.If(handler.getReference((node.getParameter(1)))).isTrue().Then(() -> {
                        handler.compile(findOutputNode(node.getParameter(2)));
                    }).Else(() -> {
                        aWhile.Break();
                    }).EndIf();
                });
                handler.compile(findOutputNode(node.getParameter(0)));
            }
        };
    }

}
