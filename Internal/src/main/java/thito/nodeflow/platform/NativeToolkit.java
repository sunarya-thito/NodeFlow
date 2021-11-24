package thito.nodeflow.platform;

import com.sun.javafx.*;
import thito.nodeflow.platform.unknown.*;
import thito.nodeflow.platform.windows.*;
import thito.nodeflow.ui.Window;

public interface NativeToolkit {
    NativeToolkit TOOLKIT = createToolkit();

    static NativeToolkit createToolkit() {
        if (PlatformUtil.isWindows()) {
            return new Win32_NativeToolkit();
        }
        return new Unknown_NativeToolkit();
    }

    void registerNativeWindowHandling(Window window);
}
