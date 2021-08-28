import org.objectweb.asm.*;
import org.objectweb.asm.util.*;

import java.io.*;
import java.lang.annotation.*;

public class ASMInspect {
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Test {
        String[] value();
        A[] a();
        int[] c();
    }
    enum A {
        B, C;
    }
    @Test(value = {"test", "test2"},a = {A.B, A.C},c = {2, 4})
    public static void main(String[] args) throws Throwable {
        ClassReader visitor = new ClassReader(ASMInspect.class.getName());
        visitor.accept(new TraceClassVisitor(null, new ASMifier(), new PrintWriter(System.out)), 0);
    }
}
