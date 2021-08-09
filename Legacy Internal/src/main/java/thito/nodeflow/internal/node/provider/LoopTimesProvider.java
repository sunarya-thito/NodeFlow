package thito.nodeflow.internal.node.provider;

import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.internal.node.*;
import thito.reflectedbytecode.*;
import thito.reflectedbytecode.jvm.*;

public class LoopTimesProvider extends AbstractNodeProvider {
    public LoopTimesProvider(NodeProviderCategory category) {
        super("java://loop_times", "Loop", category);
        addParameter(new ExecutionNodeParameter("Execution", LinkMode.MULTIPLE, LinkMode.SINGLE));
        addParameter(new JavaNodeParameter("Loop Times", int.class, false, LinkMode.SINGLE, LinkMode.NONE));
        addParameter(new ExecutionNodeParameter("Loop Execution", LinkMode.NONE, LinkMode.SINGLE));
        addParameter(new JavaNodeParameter("Time", int.class, true, LinkMode.NONE, LinkMode.MULTIPLE));
    }

    @Override
    public NodeCompileSession createCompileSession(Node node) {
        return null;
    }

    @Override
    public CompileSession createNewSession() {
        return new CompileSession() {
            @Override
            protected void prepareStructure() {
                getHandler().prepareLocalField(this, getNode().getParameter(1));
                getHandler().computeField(getNode().getParameter(3));
                getHandler().prepareStructure(findOutputNode(getNode().getParameter(0)));
                getHandler().prepareStructure(findOutputNode(getNode().getParameter(2)));
            }

            @Override
            protected void handleCompile() {
                getHandler().setReference(getNode().getParameter(3), 0);
                Java.Loop(aWhile -> {
                    Java.If(getHandler().getReference(getNode().getParameter(3))).isLessThan(
                            getHandler().getReference(getNode().getParameter(1))
                    ).Then(() -> {
                        getHandler().compile(findOutputNode(getNode().getParameter(2)));
                        getHandler().setReference(getNode().getParameter(3),
                                Reference.javaToReference(getHandler().getReference(getNode().getParameter(3))).mathAdd(1));
                    }).Else(() -> {
                        aWhile.Break();
                    }).EndIf();
                });
                getHandler().compile(findOutputNode(getNode().getParameter(0)));
            }
        };
    }
}
