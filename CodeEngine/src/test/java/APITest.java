import org.objectweb.asm.*;
import thito.nodeflow.java.*;
import thito.nodeflow.java.generated.*;
import thito.nodeflow.java.util.*;
import thito.nodeflow.java.util.Array;
import thito.nodeflow.java.util.Math;

import java.io.*;
import java.lang.reflect.*;
import java.nio.file.*;

public class APITest {
    public static void main(String[] args) throws Throwable {
        try (Context context = Context.open()) {
            GClass clazz = context.declareClass(null, "Test");
            clazz.declareMethod("nothing", Java.Class(String.class), Java.Class(Integer.class))
                    .setModifier(Modifier.STATIC | Modifier.PUBLIC)
                    .setBody(body -> {
                        LField local = body.createLocal(Java.Class(String.class));
                        local.set(Java.Class(Object.class).getConstructor().newInstance());
                        LField local2 = body.createLocal(Java.Class(int.class));
                        local2.set(Long.valueOf(1294213414314321L));
                        local2.set(124);
                        LField local3 = body.createLocal(Java.Class(Integer.class));
                        local3.set(124.421d);
                        local2.set(local3.get());
                        While.Loop(loop -> {
                            If.IsTrue(Condition.Is(local3.get()).LessThan(1000)).Then(() -> {
                                loop.Break();
                            }).End();
                        });
                        If.IsTrue(false).Then(() -> {
                            local3.set(Math.add(10, Math.divide(Math.modulo(12, 1), 4231d)));
                        }).End();
                        If.IsFalse(false).Then(() -> {
                            Java.Class(System.class).field("out").method("println", Java.Class(Object.class)).invokeVoid(local.get());
                        }).Else(() -> {
                            Java.Class(System.class).field("out").method("println", Java.Class(Object.class)).invokeVoid(
                                    Array.get(EnumType.Enum(Java.Class(Testificate.class)).values(), 0)
                            );
                        });
                    });
            System.out.println(context.writeClassSourceCode(clazz));
            Files.write(new File("test.class").toPath(), context.writeClassByteCode(clazz, Opcodes.V1_8));
            ContextClassLoader ccl = new ContextClassLoader();
            Class<?> cls = ccl.loadClass(clazz);
            Method method = cls.getDeclaredMethod("nothing", String.class, Integer.class);
            method.setAccessible(true);
            method.invoke(null, "test", 12344);
        }
    }

    public enum Testificate {
        ANOTHER, OTHER;
    }
}
