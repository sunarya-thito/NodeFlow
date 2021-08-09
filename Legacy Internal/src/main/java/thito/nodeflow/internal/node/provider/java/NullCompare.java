package thito.nodeflow.internal.node.provider.java;

import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.internal.node.*;
import thito.nodeflow.internal.node.provider.*;
import thito.reflectedbytecode.*;
import thito.reflectedbytecode.jvm.*;

public class NullCompare extends AbstractNodeProvider {
    public NullCompare(NodeProviderCategory category) {
        super("java#is_null", "Is Null", category);
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
                getHandler().setReference(getNode().getParameter(1), new Reference(boolean.class) {
                    @Override
                    protected void write() {
                        Java.If(getHandler().getReference(getNode().getParameter(0))).isNull()
                                .Then(() -> {
                                    Java.Logic.True().write(boolean.class);
                                }).Else(() -> {
                            Java.Logic.False().write(boolean.class);
                        }).EndIf();
                    }
                });
            }
        };
    }

    @Override
    public NodeCompileSession createCompileSession(Node node) {
        return new NodeCompileSession() {
            @Override
            public void handleCompile(NodeCompileHandler handler) {
                if (isParameterUsed(node.getParameter(1))) {
                    handler.putReference(node.getParameter(1), new Reference(boolean.class) {
                        @Override
                        protected void write() {
                            Java.If(handler.getReference((node.getParameter(0)))).isNull()
                                    .Then(() -> {
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
