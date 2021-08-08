package thito.nodeflow.bytecode.util;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import thito.nodeflow.bytecode.*;

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
            MethodContext context = MethodContext.getContext();
            BCHelper.writeToContext(Java.Class(boolean.class), state);
            Label end = new Label();
            context.pushNode(new JumpInsnNode(code, new LabelNode(end)));
            runnable.run();
            context.pushNode(new LabelNode(end));
        }
        public void Else(Runnable e) {
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
        }
    }
}
