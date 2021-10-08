import org.jetbrains.annotations.*;
import thito.nodeflow.java.AbstractClass;
import thito.nodeflow.java.IClass;
import thito.nodeflow.java.IConstructor;
import thito.nodeflow.java.generated.GClass;
import thito.nodeflow.java.known.KClass;

import java.io.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Set;

public class ASMInspect<T extends AbstractClass & IConstructor & IClass> {
    public <@test K extends Throwable> void test(T test, K test2, List<?> list, Object obj, Set<? extends String> set) throws K {
        test.getName();
    }

    public void test(T test) {

    }

    public static class Ext extends ASMInspect {
        @Override
        public void test(AbstractClass test) {
            super.test(test);
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE_USE)
    public @interface test {

    }
}
