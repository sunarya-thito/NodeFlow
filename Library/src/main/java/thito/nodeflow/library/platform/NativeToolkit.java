package thito.nodeflow.library.platform;

import com.sun.javafx.*;
import thito.nodeflow.library.platform.unknown.*;
import thito.nodeflow.library.ui.*;
import thito.nodeflow.library.platform.windows.*;

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
