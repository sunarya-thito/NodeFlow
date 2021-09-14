package thito.nodeflow.java;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import thito.nodeflow.java.generated.*;
import thito.nodeflow.java.generated.body.*;

import java.util.*;

public class MethodContext implements AutoCloseable {
    private static ThreadLocal<MethodContext> context = new ThreadLocal<>();

    public static MethodContext open(BodyAccessor accessor) {
        MethodContext context = MethodContext.context.get();
        if (context != null) throw new IllegalStateException("already open");
        context = new MethodContext(accessor);
        MethodContext.context.set(context);
        return context;
    }

    public static MethodContext getContext() {
        MethodContext context = MethodContext.context.get();
        if (context == null) throw new IllegalStateException("no context");
        return context;
    }

    public static boolean hasContext() {
        return MethodContext.context.get() != null;
    }

    private List<TryCatchBlockNode> tryCatchBlockNodeList = new ArrayList<>();
    private List<AbstractInsnNode> insnNodeList = new ArrayList<>();
    private BodyAccessor accessor;

    public MethodContext(BodyAccessor accessor) {
        this.accessor = accessor;
    }

    public BodyAccessor getAccessor() {
        return accessor;
    }

    public void write(MethodVisitor visitor) {
        for (TryCatchBlockNode node : tryCatchBlockNodeList) {
            node.accept(visitor);
        }
        for (AbstractInsnNode node : insnNodeList) {
            node.accept(visitor);
        }
        if (!insnNodeList.isEmpty()) {
            AbstractInsnNode node = insnNodeList.get(insnNodeList.size() - 1);
            if (node instanceof InsnNode) {
                int code = node.getOpcode();
                if (code == Opcodes.RETURN || code == Opcodes.IRETURN || code == Opcodes.ARETURN ||
                code == Opcodes.DRETURN || code == Opcodes.FRETURN || code == Opcodes.LRETURN) {
                    return;
                }
            }
        }
        if (accessor instanceof MethodBodyAccessor) {
            GMethod method = ((MethodBodyAccessor) accessor).getDeclaringMember();
            if (method.isVoid()) {
                visitor.visitInsn(Opcodes.RETURN);
            } else throw new IllegalStateException("no return value for method "+method);
        } else {
            visitor.visitInsn(Opcodes.RETURN);
        }
    }

    public void pushNode(TryCatchBlockNode node) {
        tryCatchBlockNodeList.add(node);
    }

    public void pushNode(AbstractInsnNode node) {
        insnNodeList.add(node);
    }

    public void close() {
        if (context.get() == this) {
            context.set(null);
        } else throw new IllegalStateException("already closed");
    }
}
