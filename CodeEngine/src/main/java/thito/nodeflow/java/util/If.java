package thito.nodeflow.java.util;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import thito.nodeflow.java.*;

public class If {
    private Object state;
    private int code;

    public If(Object state, int code) {
        this.state = state;
        this.code = code;
    }

    public static If IsTrue(Object state) {
        return new If(state, Opcodes.IFNE);
    }
    public static If IsFalse(Object state) {
        return new If(state, Opcodes.IFEQ);
    }
    public Then Then(Runnable run) {
        return new Then(run);
    }
    public class Then {
        private Runnable runnable;

        public Then(Runnable runnable) {
            this.runnable = runnable;
        }

        public void End() {
            if (MethodContext.hasContext()) {
                MethodContext context = MethodContext.getContext();
                BCHelper.writeToContext(Java.Class(boolean.class), state);
                Label end = new Label();
                context.pushNode(new JumpInsnNode(code, new LabelNode(end)));
                runnable.run();
                context.pushNode(new LabelNode(end));
            } else if (SourceCode.hasContext()) {
                SourceCode sourceCode = SourceCode.getContext();
                // DO NOT CREATE VARIABLE FOR sourceCode.getLine()
                // sourceCode.getLine() changes everytime endLine() called!
                sourceCode.getLine().append("if (");
                if (code == Opcodes.IFEQ) {
                    sourceCode.getLine().append("!(");
                    BCHelper.writeToSourceCode(Java.Class(boolean.class), state);
                    sourceCode.getLine().append(')');
                } else {
                    BCHelper.writeToSourceCode(Java.Class(boolean.class), state);
                }
                sourceCode.getLine().append(") {");
                sourceCode.endLine();
                sourceCode.incIndent();
                runnable.run();
                sourceCode.decIndent();
                sourceCode.getLine().append("}");
                sourceCode.endLine();
            } else throw new IllegalStateException("no context");
        }
        public void Else(Runnable e) {
            if (MethodContext.hasContext()) {
                MethodContext context = MethodContext.getContext();
                BCHelper.writeToContext(Java.Class(boolean.class), state);
                Label other = new Label();
                Label end = new Label();
                context.pushNode(new JumpInsnNode(code, new LabelNode(other)));
                runnable.run();
                context.pushNode(new JumpInsnNode(Opcodes.GOTO, new LabelNode(end)));
                context.pushNode(new LabelNode(other));
                e.run();
                context.pushNode(new LabelNode(end));
            } else if (SourceCode.hasContext()) {
                SourceCode sourceCode = SourceCode.getContext();
                // DO NOT CREATE VARIABLE FOR sourceCode.getLine()
                // sourceCode.getLine() changes everytime endLine() called!
                sourceCode.getLine().append("if (");
                if (code == Opcodes.IFEQ) {
                    sourceCode.getLine().append("!(");
                    BCHelper.writeToSourceCode(Java.Class(boolean.class), state);
                    sourceCode.getLine().append(')');
                } else {
                    BCHelper.writeToSourceCode(Java.Class(boolean.class), state);
                }
                sourceCode.getLine().append(") {");
                sourceCode.endLine();
                sourceCode.incIndent();
                runnable.run();
                sourceCode.decIndent();
                sourceCode.getLine().append("} else {");
                sourceCode.endLine();
                sourceCode.incIndent();
                e.run();
                sourceCode.decIndent();
                sourceCode.getLine().append('}');
                sourceCode.endLine();
            } else throw new IllegalStateException("no context");
        }
    }
}
