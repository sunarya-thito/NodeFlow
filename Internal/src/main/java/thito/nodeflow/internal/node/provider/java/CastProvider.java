package thito.nodeflow.internal.node.provider.java;

import org.objectweb.asm.*;
import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.internal.node.*;
import thito.nodeflow.internal.node.provider.*;
import thito.reflectedbytecode.*;
import thito.reflectedbytecode.jvm.*;

public class CastProvider extends AbstractNodeProvider {
    private Class<?> type;
    public CastProvider(Class<?> type, NodeProviderCategory category) {
        super("java://cast#"+type.getName(), "Cast to "+type.getSimpleName(), category);
        this.type = type;
        addParameter(new JavaNodeParameter("Target", Object.class, false, LinkMode.SINGLE, LinkMode.NONE));
        addParameter(new JavaNodeParameter("Result", type, true, LinkMode.NONE, LinkMode.MULTIPLE));
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
                getHandler().setReference(getNode().getParameter(1), Java.Cast(Reference.javaToReference(
                        getHandler().getReference(getNode().getParameter(0))
                ), type));
            }
        };
    }

    @Override
    public NodeCompileSession createCompileSession(Node node) {
        return new NodeCompileSession() {
            @Override
            protected void handleCompile(NodeCompileHandler handler) {
                if (isParameterUsed(node.getParameter(1))) {
                    handler.putReference(node.getParameter(1), new Reference(type) {
                        @Override
                        protected void write() {
                            handler.getReference(node.getParameter(0)).write(Object.class);
                            Code.getCode().getCodeVisitor().visitTypeInsn(Opcodes.CHECKCAST, Type.getType(type).getInternalName());
                        }
                    });
                }
            }
        };
    }

}
