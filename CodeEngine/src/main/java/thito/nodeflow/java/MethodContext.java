package thito.nodeflow.java;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
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
