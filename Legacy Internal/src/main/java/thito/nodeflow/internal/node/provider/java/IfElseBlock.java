package thito.nodeflow.internal.node.provider.java;

import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.internal.node.*;
import thito.nodeflow.internal.node.provider.*;
import thito.reflectedbytecode.jvm.*;

public class IfElseBlock extends AbstractNodeProvider {
    public IfElseBlock(NodeProviderCategory category) {
        super("java#ifElseBlock", "If", category);
        addParameter(new ExecutionNodeParameter("Execution", LinkMode.MULTIPLE, LinkMode.NONE));
        addParameter(new JavaNodeParameter("Condition", boolean.class, false, LinkMode.SINGLE, LinkMode.NONE));
        addParameter(new ExecutionNodeParameter("If True", LinkMode.NONE, LinkMode.SINGLE));
        addParameter(new ExecutionNodeParameter("Else", LinkMode.NONE, LinkMode.SINGLE));
    }

    @Override
    public CompileSession createNewSession() {
        return new CompileSession() {
            @Override
            protected void prepareStructure() {
                getHandler().prepareLocalField(this, getNode().getParameter(1));
                getHandler().prepareStructure(findOutputNode(getNode().getParameter(0)));
                getHandler().prepareStructure(findOutputNode(getNode().getParameter(2)));
                getHandler().prepareStructure(findOutputNode(getNode().getParameter(3)));
            }

            @Override
            protected void handleCompile() {
                Java.If(getHandler().getReference((getNode().getParameter(1)))).isTrue().Then(() -> {
                    getHandler().compile(findOutputNode(getNode().getParameter(2)));
                }).Else(() -> {
                    getHandler().compile(findOutputNode(getNode().getParameter(3)));
                }).EndIf();
                getHandler().compile(findOutputNode(getNode().getParameter(0)));
            }
        };
    }

    @Override
    public NodeCompileSession createCompileSession(Node node) {
        return new NodeCompileSession() {
            @Override
            public void handleCompile(NodeCompileHandler handler) {
                Java.If(handler.getReference((node.getParameter(1)))).isTrue().Then(() -> {
                    handler.compile(findOutputNode(node.getParameter(2)));
                }).Else(() -> {
                    handler.compile(findOutputNode(node.getParameter(3)));
                }).EndIf();
                handler.compile(findOutputNode(node.getParameter(0)));
            }
        };
    }

}
