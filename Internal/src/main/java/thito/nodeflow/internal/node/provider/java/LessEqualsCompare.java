package thito.nodeflow.internal.node.provider.java;

import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.internal.node.*;
import thito.nodeflow.internal.node.provider.*;
import thito.reflectedbytecode.*;
import thito.reflectedbytecode.jvm.*;

public class LessEqualsCompare extends AbstractNodeProvider {
    public LessEqualsCompare(NodeProviderCategory category) {
        super("java#lessequalscompare", "Is Less Than Or Equals To", category);
        addParameter(new JavaNodeParameter("Target", Number.class, false, LinkMode.SINGLE, LinkMode.NONE));
        addParameter(new JavaNodeParameter("Value", Number.class, false, LinkMode.SINGLE, LinkMode.NONE));
        addParameter(new JavaNodeParameter("Result", boolean.class, true, LinkMode.NONE, LinkMode.MULTIPLE));
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
                Java.If(getHandler().getReference((getNode().getParameter(0))))
                        .isLessOrEqualsTo(getHandler().getReference(getNode().getParameter(1))).Then(() -> {
                    getHandler().setReference(getNode().getParameter(2), Java.Logic.True());
                }).Else(() -> {
                    getHandler().setReference(getNode().getParameter(2), Java.Logic.False());
                }).EndIf();
            }
        };
    }

    @Override
    public NodeCompileSession createCompileSession(Node node) {
        return new NodeCompileSession() {
            @Override
            public void handleCompile(NodeCompileHandler handler) {
                if (isParameterUsed(node.getParameter(2))) {
                    handler.putReference(node.getParameter(2), new Reference(boolean.class) {
                        @Override
                        protected void write() {
                            Java.If(handler.getReference((node.getParameter(0))))
                                    .isLessOrEqualsTo(handler.getReference(node.getParameter(1))).Then(() -> {
                                Java.Logic.True().write(boolean.class);
                            }).Else(() -> {
                                Java.Logic.False().write(boolean.class);
                            }).EndIf();
                        }
                    });
                }
            }
        };
    }

}
