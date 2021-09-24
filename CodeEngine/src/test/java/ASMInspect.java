import org.jetbrains.annotations.*;

import java.io.*;

public class ASMInspect {
    public void a() throws Exception {
        APITest.Testificate.valueOf("test").name();
    }

    @Contract(pure = true)
    public String test() {
        return  "";
    }
}
