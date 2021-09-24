package thito.nodeflow.debugger.client;

import java.io.*;
import java.rmi.*;
import java.rmi.registry.*;
import java.util.*;
import java.util.concurrent.*;

public class JavaApplication {

    private DebuggerManager debuggerManager;
    private File file;

    private String[] extraJVMArguments;
    private String[] extraApplicationArguments;

    private int port;
    private Registry registry;

    private RemoteLogger handler;
    private Process process;
    private ExecutorService pool = Executors.newFixedThreadPool(3);

    public JavaApplication(DebuggerManager manager, int port, File file) throws RemoteException, AlreadyBoundException {
        this.file = file;
        this.port = port;
        registry = LocateRegistry.createRegistry(port);
        registry.bind(RemoteLogger.REGISTRY_NAME, manager.getRemoteLogger());
    }

    public void startProcess() throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.environment().clear();
        processBuilder.environment().put("jarLocation", file.getName());
        processBuilder.environment().put("debuggerPort", String.valueOf(port));
        List<String> commands = new ArrayList<>(Arrays.asList(extraJVMArguments));
        commands.add(debuggerManager.getJavaExecutable());
        commands.add("-jar");
        commands.add(file.getName());
        commands.addAll(Arrays.asList(extraApplicationArguments));
        processBuilder.command(commands);
        processBuilder.directory(file.getParentFile());
        process = processBuilder.start();
        pool.submit(() -> {
            while (process.isAlive() ) {

            }
        });
    }

    public Process getProcess() {
        return process;
    }
}
