package thito.nodeflow.internal.node.provider.java;

import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.internal.node.*;
import thito.nodeflow.internal.node.provider.*;
import thito.reflectedbytecode.*;
import thito.reflectedbytecode.jvm.*;

public class TernaryBlock extends AbstractNodeProvider {
    private Class<?> type;
    public TernaryBlock(Class<?> clazz, NodeProviderCategory category) {
        super("java#ternary://"+clazz.getName(), "Ternary Value", category);
        type = clazz;
        addParameter(new JavaNodeParameter("Condition", boolean.class, false, LinkMode.SINGLE, LinkMode.NONE));
        addParameter(new JavaNodeParameter("True Value", clazz, false, LinkMode.SINGLE, LinkMode.NONE));
        addParameter(new JavaNodeParameter("False Value", clazz, false, LinkMode.SINGLE, LinkMode.NONE));
        addParameter(new JavaNodeParameter("Final Value", clazz, true, LinkMode.NONE, LinkMode.MULTIPLE));
    }

    @Override
    public CompileSession createNewSession() {
        return new CompileSession() {
            @Override
            protected void prepareStructure() {
                getHandler().prepareLocalField(this, getNode().getParameter(0));
                getHandler().prepareLocalField(this, getNode().getParameter(1));
                getHandler().prepareLocalField(this, getNode().getParameter(2));
                getHandler().computeField(getNode().getParameter(3));
            }

            @Override
            protected void handleCompile() {
                getHandler().setReference(getNode().getParameter(3), new Reference(type) {
                    @Override
                    protected void write() {
                        Java.If(getHandler().getReference((getNode().getParameter(0)))).isTrue().Then(() -> {
                            Reference.handleWrite(type, getHandler().getReference((getNode().getParameter(1))));
                        }).Else(() -> {
                            Reference.handleWrite(type, getHandler().getReference(getNode().getParameter(2)));
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
                if (isParameterUsed(node.getParameter(3))) {
                    handler.putReference(node.getParameter(3), new Reference(type) {
                        @Override
                        protected void write() {
                            Java.If(handler.getReference((node.getParameter(0)))).isTrue().Then(() -> {
                                handler.getReference((node.getParameter(1))).write(type);
                            }).Else(() -> {
                                handler.getReference((node.getParameter(2))).write(type);
                            }).EndIf();
                        }
                    });
                }
            }
        };
    }

}
