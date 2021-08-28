package thito.nodeflow.java.util;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import thito.nodeflow.java.*;

import java.util.function.*;

public class While {
    public static void Loop(Consumer<While> loopConsumer) {
        if (MethodContext.hasContext()) {
            MethodContext context = MethodContext.getContext();
            Label end = new Label();
            Label repeat = new Label();
            context.pushNode(new LabelNode(repeat));
            loopConsumer.accept(new While(end, repeat));
            context.pushNode(new JumpInsnNode(Opcodes.GOTO, new LabelNode(repeat)));
            context.pushNode(new LabelNode(end));
        } else if (SourceCode.hasContext()) {
            SourceCode sourceCode = SourceCode.getContext();
            sourceCode.getLine().append("while (true) {");
            sourceCode.endLine();
            sourceCode.incIndent();
            loopConsumer.accept(new While(null, null));
            sourceCode.decIndent();
            sourceCode.getLine().append("}");
            sourceCode.endLine();
        } else throw new IllegalStateException("no context");
    }

    private Label end, repeat;

    public While(Label end, Label repeat) {
        this.end = end;
        this.repeat = repeat;
    }

    public void Break() {
        if (MethodContext.hasContext()) {
            MethodContext.getContext().pushNode(new JumpInsnNode(Opcodes.GOTO, new LabelNode(end)));
        } else if (SourceCode.hasContext()) {
            SourceCode code = SourceCode.getContext();
            code.getLine().append("break;");
            code.endLine();
        } else throw new IllegalStateException("no context");
    }

    public void Continue() {
        if (MethodContext.hasContext()) {
            MethodContext.getContext().pushNode(new JumpInsnNode(Opcodes.GOTO, new LabelNode(repeat)));
        } else if (SourceCode.hasContext()) {
            SourceCode context = SourceCode.getContext();
            context.getLine().append("continue;");
            context.endLine();
        } else throw new IllegalStateException("no context");
    }
}
