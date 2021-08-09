package thito.nodeflow.internal;

import java.io.*;
import java.util.function.*;

public class UnsupportedDeviceError extends Error {
    public UnsupportedDeviceError() {
        super();
    }

    public UnsupportedDeviceError(String message) {
        super(message);
    }

    public UnsupportedDeviceError(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedDeviceError(Throwable cause) {
        super(cause);
    }

    @Override
    public void printStackTrace() {
        super.printStackTrace();
        printDeviceInfo(System.err::println);
    }

    @Override
    public void printStackTrace(PrintStream s) {
        super.printStackTrace(s);
        printDeviceInfo(s::println);
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        super.printStackTrace(s);
        printDeviceInfo(s::println);
    }

    private void printDeviceInfo(Consumer<Object> printer) {
    }
}
