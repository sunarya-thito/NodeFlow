import thito.nodeflow.java.*;
import thito.nodeflow.java.generated.*;
import thito.nodeflow.java.util.*;

public class APITest {
    public static void main(String[] args) {
        try (Context context = Context.open()) {
            GClass clazz = context.declareClass(null, "Test");
            clazz.declareMethod("nothing", Java.Class(String.class), Java.Class(Integer.class))
                    .setBody(body -> {
                        LField local = body.createLocal(Java.Class(String.class));
//                        local.set("weird");
                        If.IsTrue(false).Then(() -> {

                        }).End();
                        If.IsFalse(false).Then(() -> {
                            Java.Class(System.class).field("out").method("println", Java.Class(Object.class)).invokeVoid(local.get());
                        }).Else(() -> {
                            Java.Class(System.class).field("out").method("println", Java.Class(Object.class)).invokeVoid("test");
                        });
                    });
            System.out.println(context.writeClassSourceCode(clazz));
        }
    }
}
