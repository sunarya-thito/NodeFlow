package thito.nodeflow.java.util;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import thito.nodeflow.java.*;

public class Condition {
    public static Condition Is(Object obj) {
        return new Condition(obj);
    }

    private Object object;

    public Condition(Object object) {
        this.object = object;
    }

    public Reference NotNull() {
        return new Reference(boolean.class) {
            @Override
            public void write() {
                MethodContext context = MethodContext.getContext();
                Label other = new Label();
                Label end = new Label();
                BCHelper.writeToContext(Java.Class(Object.class), object);
                context.pushNode(new JumpInsnNode(Opcodes.IFNULL, new LabelNode(other)));
                context.pushNode(new InsnNode(Opcodes.ICONST_0));
                context.pushNode(new JumpInsnNode(Opcodes.GOTO, new LabelNode(end)));
                context.pushNode(new LabelNode(other));
                context.pushNode(new InsnNode(Opcodes.ICONST_1));
                context.pushNode(new LabelNode(end));
            }

            @Override
            public void writeSourceCode() {
                StringBuilder line = SourceCode.getContext().getLine();
                line.append('(');
                BCHelper.writeToSourceCode(Java.Class(Object.class), object);
                line.append(" != null");
                line.append(')');
            }
        };
    }

    public Reference Null() {
        return new Reference(boolean.class) {
            @Override
            public void write() {
                MethodContext context = MethodContext.getContext();
                Label other = new Label();
                Label end = new Label();
                BCHelper.writeToContext(Java.Class(Object.class), object);
                context.pushNode(new JumpInsnNode(Opcodes.IFNONNULL, new LabelNode(other)));
                context.pushNode(new InsnNode(Opcodes.ICONST_0));
                context.pushNode(new JumpInsnNode(Opcodes.GOTO, new LabelNode(end)));
                context.pushNode(new LabelNode(other));
                context.pushNode(new InsnNode(Opcodes.ICONST_1));
                context.pushNode(new LabelNode(end));
            }

            @Override
            public void writeSourceCode() {
                StringBuilder line = SourceCode.getContext().getLine();
                line.append('(');
                BCHelper.writeToSourceCode(Java.Class(Object.class), object);
                line.append(" == null");
                line.append(')');
            }
        };
    }

    private void validateNumbers(Object compare) {
        if (!Java.Class(Number.class).isAssignableFrom(BCHelper.getType(object)) ||
            !Java.Class(Number.class).isAssignableFrom(BCHelper.getType(compare))) {
            throw new IllegalArgumentException("compared objects must be a number");
        }
    }

    public Reference greaterThan(Object compare) {
        validateNumbers(compare);
        return new Reference(boolean.class) {
            @Override
            public void write() {
                MethodContext context = MethodContext.getContext();
                Label other = new Label();
                Label end = new Label();
                IClass priority = BCHelper.getPrioritized(BCHelper.getType(object), BCHelper.getType(compare));
                priority = BCHelper.wrapperToPrimitive(priority);
                BCHelper.writeToContext(priority, object);
                BCHelper.writeToContext(priority, compare);
                if (priority.getName().equals("long")) {
                    context.pushNode(new InsnNode(Opcodes.LCMP));
                } else if (priority.getName().equals("double")) {
                    context.pushNode(new InsnNode(Opcodes.DCMPL));
                }
                context.pushNode(new JumpInsnNode(Opcodes.IF_ICMPLE, new LabelNode(other)));
                context.pushNode(new InsnNode(Opcodes.ICONST_0));
                context.pushNode(new JumpInsnNode(Opcodes.GOTO, new LabelNode(end)));
                context.pushNode(new LabelNode(other));
                context.pushNode(new InsnNode(Opcodes.ICONST_1));
                context.pushNode(new LabelNode(end));
            }

            @Override
            public void writeSourceCode() {
                IClass priority = BCHelper.getPrioritized(BCHelper.getType(object), BCHelper.getType(compare));
                StringBuilder line = SourceCode.getContext().getLine();
                line.append('(');
                BCHelper.writeToSourceCode(priority, object);
                line.append(" > ");
                BCHelper.writeToSourceCode(priority, compare);
                line.append(')');
            }
        };
    }

    public Reference SameInstance(Object compare) {
        if (compare == null) return Null();
        return new Reference(boolean.class) {
            @Override
            public void write() {
                MethodContext context = MethodContext.getContext();
                Label other = new Label();
                Label end = new Label();
                BCHelper.writeToContext(Java.Class(Object.class), object);
                BCHelper.writeToContext(Java.Class(Object.class), compare);
                context.pushNode(new JumpInsnNode(Opcodes.IF_ACMPNE, new LabelNode(other)));
                context.pushNode(new InsnNode(Opcodes.ICONST_0));
                context.pushNode(new JumpInsnNode(Opcodes.GOTO, new LabelNode(end)));
                context.pushNode(new LabelNode(other));
                context.pushNode(new InsnNode(Opcodes.ICONST_1));
                context.pushNode(new LabelNode(end));
            }

            @Override
            public void writeSourceCode() {
                StringBuilder line = SourceCode.getContext().getLine();
                line.append('(');
                BCHelper.writeToSourceCode(Java.Class(Object.class), object);
                line.append(" == ");
                BCHelper.writeToSourceCode(Java.Class(Object.class), compare);
                line.append(')');
            }
        };
    }

    public Reference DifferentInstance(Object compare) {
        if (compare == null) return NotNull();
        return new Reference(boolean.class) {
            @Override
            public void write() {
                MethodContext context = MethodContext.getContext();
                Label other = new Label();
                Label end = new Label();
                BCHelper.writeToContext(Java.Class(Object.class), object);
                BCHelper.writeToContext(Java.Class(Object.class), compare);
                context.pushNode(new JumpInsnNode(Opcodes.IF_ACMPEQ, new LabelNode(other)));
                context.pushNode(new InsnNode(Opcodes.ICONST_0));
                context.pushNode(new JumpInsnNode(Opcodes.GOTO, new LabelNode(end)));
                context.pushNode(new LabelNode(other));
                context.pushNode(new InsnNode(Opcodes.ICONST_1));
                context.pushNode(new LabelNode(end));
            }


            @Override
            public void writeSourceCode() {
                StringBuilder line = SourceCode.getContext().getLine();
                line.append('(');
                BCHelper.writeToSourceCode(Java.Class(Object.class), object);
                line.append(" != ");
                BCHelper.writeToSourceCode(Java.Class(Object.class), compare);
                line.append(')');
            }
        };
    }

    public Reference LessThan(Object compare) {
        validateNumbers(compare);
        return new Reference(boolean.class) {
            @Override
            public void write() {
                MethodContext context = MethodContext.getContext();
                Label other = new Label();
                Label end = new Label();
                IClass priority = BCHelper.getPrioritized(BCHelper.getType(object), BCHelper.getType(compare));
                priority = BCHelper.wrapperToPrimitive(priority);
                BCHelper.writeToContext(priority, object);
                BCHelper.writeToContext(priority, compare);
                if (priority.getName().equals("long")) {
                    context.pushNode(new InsnNode(Opcodes.LCMP));
                } else if (priority.getName().equals("double")) {
                    context.pushNode(new InsnNode(Opcodes.DCMPG));
                }
                context.pushNode(new JumpInsnNode(Opcodes.IF_ICMPGE, new LabelNode(other)));
                context.pushNode(new InsnNode(Opcodes.ICONST_0));
                context.pushNode(new JumpInsnNode(Opcodes.GOTO, new LabelNode(end)));
                context.pushNode(new LabelNode(other));
                context.pushNode(new InsnNode(Opcodes.ICONST_1));
                context.pushNode(new LabelNode(end));
            }

            @Override
            public void writeSourceCode() {
                IClass priority = BCHelper.getPrioritized(BCHelper.getType(object), BCHelper.getType(compare));
                StringBuilder line = SourceCode.getContext().getLine();
                line.append('(');
                BCHelper.writeToSourceCode(priority, object);
                line.append(" < ");
                BCHelper.writeToSourceCode(priority, compare);
                line.append(')');
            }
        };
    }

    public Reference EqualTo(Object compare) {
        validateNumbers(compare);
        return new Reference(boolean.class) {
            @Override
            public void write() {
                MethodContext context = MethodContext.getContext();
                Label other = new Label();
                Label end = new Label();
                IClass priority = BCHelper.getPrioritized(BCHelper.getType(object), BCHelper.getType(compare));
                priority = BCHelper.wrapperToPrimitive(priority);
                BCHelper.writeToContext(priority, object);
                BCHelper.writeToContext(priority, compare);
                if (priority.getName().equals("long")) {
                    context.pushNode(new InsnNode(Opcodes.LCMP));
                } else if (priority.getName().equals("double")) {
                    context.pushNode(new InsnNode(Opcodes.DCMPG)); // DCMPG or DCMPL doesn't matter
                }
                context.pushNode(new JumpInsnNode(Opcodes.IF_ICMPNE, new LabelNode(other)));
                context.pushNode(new InsnNode(Opcodes.ICONST_0));
                context.pushNode(new JumpInsnNode(Opcodes.GOTO, new LabelNode(end)));
                context.pushNode(new LabelNode(other));
                context.pushNode(new InsnNode(Opcodes.ICONST_1));
                context.pushNode(new LabelNode(end));
            }

            @Override
            public void writeSourceCode() {
                IClass priority = BCHelper.getPrioritized(BCHelper.getType(object), BCHelper.getType(compare));
                StringBuilder line = SourceCode.getContext().getLine();
                line.append('(');
                BCHelper.writeToSourceCode(priority, object);
                line.append(" == ");
                BCHelper.writeToSourceCode(priority, compare);
                line.append(')');
            }
        };
    }

    public Reference NotEqualTo(Object compare) {
        validateNumbers(compare);
        return new Reference(boolean.class) {
            @Override
            public void write() {
                MethodContext context = MethodContext.getContext();
                Label other = new Label();
                Label end = new Label();
                IClass priority = BCHelper.getPrioritized(BCHelper.getType(object), BCHelper.getType(compare));
                priority = BCHelper.wrapperToPrimitive(priority);
                BCHelper.writeToContext(priority, object);
                BCHelper.writeToContext(priority, compare);
                if (priority.getName().equals("long")) {
                    context.pushNode(new InsnNode(Opcodes.LCMP));
                } else if (priority.getName().equals("double")) {
                    context.pushNode(new InsnNode(Opcodes.DCMPG));
                }
                context.pushNode(new JumpInsnNode(Opcodes.IF_ICMPEQ, new LabelNode(other)));
                context.pushNode(new InsnNode(Opcodes.ICONST_0));
                context.pushNode(new JumpInsnNode(Opcodes.GOTO, new LabelNode(end)));
                context.pushNode(new LabelNode(other));
                context.pushNode(new InsnNode(Opcodes.ICONST_1));
                context.pushNode(new LabelNode(end));
            }

            @Override
            public void writeSourceCode() {
                IClass priority = BCHelper.getPrioritized(BCHelper.getType(object), BCHelper.getType(compare));
                StringBuilder line = SourceCode.getContext().getLine();
                line.append('(');
                BCHelper.writeToSourceCode(priority, object);
                line.append(" != ");
                BCHelper.writeToSourceCode(priority, compare);
                line.append(')');
            }
        };
    }

    public Reference GreaterThanOrEqualTo(Object compare) {
        validateNumbers(compare);
        return new Reference(boolean.class) {
            @Override
            public void write() {
                MethodContext context = MethodContext.getContext();
                Label other = new Label();
                Label end = new Label();
                IClass priority = BCHelper.getPrioritized(BCHelper.getType(object), BCHelper.getType(compare));
                priority = BCHelper.wrapperToPrimitive(priority);
                BCHelper.writeToContext(priority, object);
                BCHelper.writeToContext(priority, compare);
                if (priority.getName().equals("long")) {
                    context.pushNode(new InsnNode(Opcodes.LCMP));
                } else if (priority.getName().equals("double")) {
                    context.pushNode(new InsnNode(Opcodes.DCMPL));
                }
                context.pushNode(new JumpInsnNode(Opcodes.IF_ICMPLT, new LabelNode(other)));
                context.pushNode(new InsnNode(Opcodes.ICONST_0));
                context.pushNode(new JumpInsnNode(Opcodes.GOTO, new LabelNode(end)));
                context.pushNode(new LabelNode(other));
                context.pushNode(new InsnNode(Opcodes.ICONST_1));
                context.pushNode(new LabelNode(end));
            }

            @Override
            public void writeSourceCode() {
                IClass priority = BCHelper.getPrioritized(BCHelper.getType(object), BCHelper.getType(compare));
                StringBuilder line = SourceCode.getContext().getLine();
                line.append('(');
                BCHelper.writeToSourceCode(priority, object);
                line.append(" >= ");
                BCHelper.writeToSourceCode(priority, compare);
                line.append(')');
            }
        };
    }

    public Reference LessThanOrEqualTo(Object compare) {
        validateNumbers(compare);
        return new Reference(boolean.class) {
            @Override
            public void write() {
                MethodContext context = MethodContext.getContext();
                Label other = new Label();
                Label end = new Label();
                IClass priority = BCHelper.getPrioritized(BCHelper.getType(object), BCHelper.getType(compare));
                priority = BCHelper.wrapperToPrimitive(priority);
                BCHelper.writeToContext(priority, object);
                BCHelper.writeToContext(priority, compare);
                if (priority.getName().equals("long")) {
                    context.pushNode(new InsnNode(Opcodes.LCMP));
                } else if (priority.getName().equals("double")) {
                    context.pushNode(new InsnNode(Opcodes.DCMPG));
                }
                context.pushNode(new JumpInsnNode(Opcodes.IF_ICMPGT, new LabelNode(other)));
                context.pushNode(new InsnNode(Opcodes.ICONST_0));
                context.pushNode(new JumpInsnNode(Opcodes.GOTO, new LabelNode(end)));
                context.pushNode(new LabelNode(other));
                context.pushNode(new InsnNode(Opcodes.ICONST_1));
                context.pushNode(new LabelNode(end));
            }

            @Override
            public void writeSourceCode() {
                IClass priority = BCHelper.getPrioritized(BCHelper.getType(object), BCHelper.getType(compare));
                StringBuilder line = SourceCode.getContext().getLine();
                line.append('(');
                BCHelper.writeToSourceCode(priority, object);
                line.append(" <= ");
                BCHelper.writeToSourceCode(priority, compare);
                line.append(')');
            }
        };
    }
}
