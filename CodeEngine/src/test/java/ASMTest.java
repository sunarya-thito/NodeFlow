import org.objectweb.asm.*;

public class ASMTest {
    public static void main(String[] args) {
        System.out.println(Object[].class.getSimpleName());
        System.out.println(Type.getType(double.class).getOpcode(Opcodes.LCMP) == Opcodes.DCMPG);
    }
}
