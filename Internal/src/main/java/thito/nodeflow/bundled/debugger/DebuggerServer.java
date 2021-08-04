package thito.nodeflow.bundled.debugger;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class DebuggerServer {
    private static DebuggerServer instance = new DebuggerServer();
    /*
    actions:
    0 - not running
    1 - running
     */
    public static void report(long a, long b, long c, long d, int action) {
        instance.broadcast(a, b, c, d, action);
    }

    public static void startServer() {
        if (!instance.running) {
            try {
                instance.start();
            } catch (IOException e) {
                System.out.println("[NodeFlow-Debugger] Failed to start debugger server");
                e.printStackTrace();
            }
        }
    }

    public static void stopServer() {
        instance.stop();
    }

    private ServerSocket server;
    private ExecutorService pool = Executors.newSingleThreadExecutor();
    private List<Socket> clients = new ArrayList<>();
    private boolean running;

    public void stop() {
        running = true;
        try {
            server.close();
        } catch (Throwable e) {
        }
        for (Socket client : clients) {
            try {
                client.close();
            } catch (Throwable e) {
            }
        }
        clients.clear();
    }

    public void start() throws IOException {
        server = new ServerSocket(45050);
        running = true;
        while (running) {
            Socket client = server.accept();
            handleClient(client);
        }
    }

    private void broadcast(long most, long least, long toMost, long toLeast, int action) {
        for (Socket client : new ArrayList<>(clients)) {
            pool.execute(() -> {
                try {
                    OutputStream outputStream = client.getOutputStream();
                    DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                    dataOutputStream.writeLong(most);
                    dataOutputStream.writeLong(least);
                    dataOutputStream.writeLong(toMost);
                    dataOutputStream.writeLong(toLeast);
                    dataOutputStream.writeInt(action);
                } catch (Throwable t) {
                }
            });
        }
    }

    private void handleClient(Socket client) {
        clients.add(client);
        System.out.println("[NodeFlow-Debugger] Debugger connected: "+client.getInetAddress());
    }
}
