package thito.nodeflow.internal.platform;

import com.sun.javafx.*;
import thito.nodeflow.internal.platform.windows.*;
import thito.nodeflow.internal.ui.*;

public interface NativeToolkit {
    NativeToolkit TOOLKIT = createToolkit();

    static NativeToolkit createToolkit() {
        if (PlatformUtil.isWindows()) {
            return new Win32_NativeToolkit();
        }
        return new Unknown_NativeToolkit();
    }

    void makeBorderless(Window window);
}
