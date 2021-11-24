import thito.nodeflow.LoggerInjector;

import java.io.*;
import java.util.logging.*;

public class LoggerTest {
    public static void main(String[] args) {
        Logger root = Logger.getLogger("");
        Logger logger = Logger.getLogger("TEST");
        for (Handler h : root.getHandlers()) root.removeHandler(h);
        root.addHandler(new LoggerInjector());
        System.setOut(new PrintStream(new LoggerInjector.LoggerPrintStream(logger, Level.INFO), true));
        System.setErr(new PrintStream(new LoggerInjector.LoggerPrintStream(logger, Level.SEVERE), true));
        Thread.dumpStack();
        new Thread(() -> {
            System.out.println("Test");
            System.out.println("Test2");
        }, "test").start();
    }
}
