import org.jetbrains.annotations.*;

import java.io.*;

public class ASMInspect {
    public void a() throws Exception {
        Object[][] test = new Object[][] { {10, 10} , {"test", 10L}};
//        Object[][] test = new Object[5];
    }

    @Contract(pure = true)
    public String test() {
        return  "";
    }
}
