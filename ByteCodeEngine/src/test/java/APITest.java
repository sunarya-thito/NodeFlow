import thito.nodeflow.bytecode.*;
import thito.nodeflow.bytecode.generated.*;
import thito.nodeflow.bytecode.util.*;

public class APITest {
    public static void main(String[] args) {
        try (Context context = Context.open()) {
            GClass clazz = context.declareClass("Test");
            clazz.declareMethod("nothing", Java.Class(String.class), Java.Class(Integer.class))
                    .setBody(body -> {
                        LField local = body.createLocal(Java.Class(String.class));
                        local.set("weird");
                        If.IsTrue(Condition.Is(false).SameInstance(false)).Then(() -> {

                        }).End();
                        If.IsFalse(false).Then(() -> {

                        }).Else();
                    });
        }
    }
}
