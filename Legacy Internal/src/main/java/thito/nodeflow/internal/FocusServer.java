package thito.nodeflow.internal;

import javafx.stage.*;

import java.io.*;
import java.net.*;

public class FocusServer {
    public static Stage FOCUS_DUMMY;
    public static final int PORT = 124903;
    private static boolean running;
    public void begin() throws Exception {
        ServerSocket socket = new ServerSocket(PORT);
        while (running) {
            try (Socket sock = socket.accept()) {
                DataInputStream inputStream = new DataInputStream(sock.getInputStream());
                String command = inputStream.readUTF();
                switch (command) {
                    case "FOCUS":
                        FOCUS_DUMMY.toFront();
                        break;
                }
            }
        }
        socket.close();
    }
}
