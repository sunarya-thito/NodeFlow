package thito.nodeflow.java.util;

import org.jetbrains.annotations.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import thito.nodeflow.java.*;
import thito.nodeflow.java.generated.*;

import java.util.function.*;

public class Try {

    // what a weird yet satisfying name
    @Contract(pure = true)
    public static Try This(Runnable r) {
        return new Try(r);
    }

    private Runnable r;
    public Try(Runnable r) {
        this.r = r;
    }

    public void Catch(IClass exception, Consumer<Reference> handler) {
        if (MethodContext.hasContext()) {
            MethodContext methodContext = MethodContext.getContext();
            Label start = new Label();
            Label end = new Label();
            Label label = new Label();
            Label stop = new Label();
            methodContext.pushNode(new TryCatchBlockNode(
                    new LabelNode(start),
                    new LabelNode(end),
                    new LabelNode(label),
                    BCHelper.getClassPath(exception)));
            methodContext.pushNode(new LabelNode(start));
            r.run();
            methodContext.pushNode(new LabelNode(end));
            methodContext.pushNode(new JumpInsnNode(Opcodes.GOTO, new LabelNode(stop)));
            LField local = methodContext.getAccessor().createLocal(exception);
            methodContext.pushNode(new LabelNode(label));
            methodContext.pushNode(new VarInsnNode(Opcodes.ASTORE, local.getVirtualIndex()));
            handler.accept(local.get());
            methodContext.pushNode(new LabelNode(stop));
        } else if (SourceCode.hasContext()) {
            SourceCode sourceCode = SourceCode.getContext();
            sourceCode.getLine().append("try {");
            sourceCode.endLine();
            sourceCode.incIndent();
            r.run();
            sourceCode.decIndent();
            sourceCode.getLine().append("} catch (");
            sourceCode.getLine().append(sourceCode.simplifyType(exception));
            int index = sourceCode.requestExceptionIndex();
            sourceCode.getLine().append(" ").append(index == 0 ? "e" : "e" + index).append(") {");
            sourceCode.endLine();
            sourceCode.incIndent();
            handler.accept(new Reference(exception) {
                @Override
                public void writeByteCode() {
                    throw new UnsupportedOperationException();
                }

                @Override
                public void writeSourceCode() {
                    SourceCode.getContext().getLine().append((index == 0 ? "e" : "e" + index));
                }
            });
            sourceCode.decIndent();
            sourceCode.getLine().append("}");
            sourceCode.endLine();
        } else throw new IllegalStateException("no context");
    }


}
