package thito.nodeflow.platform;

import com.sun.javafx.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.library.ui.decoration.dialog.*;
import thito.nodeflow.library.ui.decoration.window.*;
import thito.nodeflow.platform.windows.*;

public interface NativeHandle {
    static NativeHandle createBorderlessWindowHandler(WindowBar bar) {
        if (PlatformUtil.isWindows()) {
            return new Win_BorderlessWindowHandler(bar);
        }
        throw new UnsupportedDeviceError();
    }
    @Deprecated
    static NativeHandle createBorderlessDialogHandler(DialogBase base) {
        throw new UnsupportedDeviceError();
    }

}
