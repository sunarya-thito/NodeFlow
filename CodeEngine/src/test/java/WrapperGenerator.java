import org.apache.commons.lang3.*;
import thito.nodeflow.java.*;

public class WrapperGenerator {
    public static void main(String[] args) {
        Class<?>[] source = {Integer.class, Double.class, Float.class, Long.class, Short.class, Byte.class};
        for (Class<?> aClass : source) {
            System.out.println(
                    ("\n\npackage thito.nodeflow.java.transform;\n" +
                    "import org.objectweb.asm.*;\n" +
                    "import org.objectweb.asm.tree.*;\n" +
                    "import thito.nodeflow.java.*;\n" +
                    "\n" +
                    "public class PrimitiveNumberToXWrapper implements ObjectTransformation {\n" +
                    "    @Override\n" +
                    "    public Reference transform(Reference source) {\n" +
                    "        return Java.Class(X.class).method(\"valueOf\", Java.Class(x.class)).invoke(source);\n" +
                    "    }\n" +
                    "}\n").replace("X", aClass.getSimpleName()).replace("x", aClass.getSimpleName().toLowerCase())
            );
        }
    }

}


//import org.objectweb.asm.*;
//import org.objectweb.asm.tree.*;
//import thito.nodeflow.java.*;
//
//public class PrimitiveToXWrapper implements ObjectTransformation {
//    @Override
//    public Reference transform(Reference source) {
//        return Java.Class(X.class).method("valueOf", Java.Class(x.class)).invoke(source);
//    }
//}
