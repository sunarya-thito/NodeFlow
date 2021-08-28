package thito.nodeflow.installer;

import java.io.*;

public class Bootstrap {
    public static void main(String[] args) {
        if (!new File(Main.INSTALLATION_DIR, "NodeFlow.exe").exists()) {
            Main.INSTALLATION_DIR = new File(Main.INSTALLATION_DIR, "NodeFlow").getAbsoluteFile();
        }
        System.setProperty("prism.lcdtext", "false");
        Main.launch(Main.class, args);
    }
}
