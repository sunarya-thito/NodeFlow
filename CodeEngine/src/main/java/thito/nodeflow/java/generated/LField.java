package thito.nodeflow.java.generated;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import thito.nodeflow.java.*;

public class LField {
    private IClass type;
    private int index;

    public LField(IClass type, int index) {
        this.type = type;
        this.index = index;
    }

    public IClass getType() {
        return type;
    }

    public int getVirtualIndex() {
        return index;
    }

    public Reference get() {
        return new Reference(getType()) {
            @Override
            public void writeByteCode() {
                final MethodContext context = MethodContext.getContext();
                context.pushNode(new VarInsnNode(BCHelper.getASMType(getType()).getOpcode(Opcodes.ILOAD), getVirtualIndex()));
            }

            @Override
            public void writeSourceCode() {
                SourceCode sourceCode = SourceCode.getContext();
                if (!sourceCode.getVariables().contains(index)) {
                    String typeName = getType().getName();
                    String defaultValue;
                    if (typeName.equals("int")) {
                        defaultValue = "0";
                    } else if (typeName.equals("long")) {
                        defaultValue = "0L";
                    } else if (typeName.equals("double")) {
                        defaultValue = "0D";
                    } else if (typeName.equals("short")) {
                        defaultValue = "(short) 0";
                    } else if (typeName.equals("byte")) {
                        defaultValue = "(byte) 0";
                    } else if (typeName.equals("char")) {
                        defaultValue = "(char) 0";
                    } else if (typeName.equals("float")) {
                        defaultValue = "0F";
                    } else if (typeName.equals("boolean")) {
                        defaultValue = "false";
                    } else {
                        defaultValue = "null";
                    }
                    sourceCode.getLine().append(defaultValue);
                } else {
                    sourceCode.getLine().append("var").append(index);
                }
            }
        };
    }

    public void set(Object value) {
        if (MethodContext.hasContext()) {
            final MethodContext context = MethodContext.getContext();
            BCHelper.writeToContext(getType(), value);
            context.pushNode(new VarInsnNode(BCHelper.getASMType(getType()).getOpcode(Opcodes.ISTORE), getVirtualIndex()));
        } else if (SourceCode.hasContext()) {
            SourceCode sourceCode = SourceCode.getContext();
            StringBuilder line = sourceCode.getLine();
            if (!sourceCode.getVariables().contains(index)) {
                line.append(sourceCode.simplifyType(getType()));
                line.append(' ');
                sourceCode.getVariables().add(index);
            }
            line.append("var").append(index).append(" = ");
            BCHelper.writeToSourceCode(getType(), value);
            line.append(';');
            sourceCode.endLine();
        } else throw new IllegalStateException("no context");
    }
}
