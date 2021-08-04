package thito.nodeflow.bootstrap;

import thito.nodeflow.launcher.*;

import java.io.*;

public class Bootstrap {
    public static void main(String[] args) throws Throwable {
        System.setProperty("prism.vsync", "false");
//        System.setProperty("prism.order", "es2,es1,sw,j2d");
        System.setProperty("prism.forceGPU", "true");
//        System.setProperty("sun.java2d.opengl", "true");
        PrintStream backupPrinter = System.out;
        try {
            Main.main(args);
        } catch (Throwable t) {
            t.printStackTrace(backupPrinter);
            crashHandler(t);
            Launcher.error("Crash", "File \"crash.log\" has been created!");
        }
    }

    public static void crashHandler(Throwable throwable) throws Throwable {
        try (PrintStream printStream = new PrintStream(new FileOutputStream("crash.log"))) {
            throwable.printStackTrace(printStream);
        }
    }
}
