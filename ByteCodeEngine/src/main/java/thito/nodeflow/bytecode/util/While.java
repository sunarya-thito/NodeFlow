package thito.nodeflow.bytecode.util;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import thito.nodeflow.bytecode.*;

import java.util.function.*;

public class While {
    public static void Loop(Consumer<While> loopConsumer) {
        MethodContext context = MethodContext.getContext();
        Label end = new Label();
        Label repeat = new Label();
        context.pushNode(new LabelNode(repeat));
        loopConsumer.accept(new While(end, repeat));
        context.pushNode(new JumpInsnNode(Opcodes.GOTO, new LabelNode(repeat)));
        context.pushNode(new LabelNode(end));
    }

    private Label end, repeat;

    public While(Label end, Label repeat) {
        this.end = end;
        this.repeat = repeat;
    }

    public void Break() {
        MethodContext.getContext().pushNode(new JumpInsnNode(Opcodes.GOTO, new LabelNode(end)));
    }

    public void Continue() {
        MethodContext.getContext().pushNode(new JumpInsnNode(Opcodes.GOTO, new LabelNode(repeat)));
    }
}
