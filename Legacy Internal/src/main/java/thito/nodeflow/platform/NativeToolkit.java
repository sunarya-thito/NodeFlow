package thito.nodeflow.platform;

import com.sun.javafx.*;
import javafx.stage.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.platform.windows.*;

public interface NativeToolkit {
    NativeToolkit TOOLKIT = createToolkit();
    static NativeToolkit createToolkit() {
        if (PlatformUtil.isWindows()) {
            return new Win_Toolkit();
        }
        throw new UnsupportedDeviceError();
    }
    void makeFullyTransparent(Stage stage);
}
