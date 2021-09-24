package thito.nodeflow.debugger.client;

import java.io.*;
import java.util.*;

public class DebuggerManager {
    private static DebuggerManager instance;

    public static DebuggerManager getInstance() {
        return instance;
    }

    private RemoteLogger remoteLogger;
    private List<JavaApplication> runningApps = new ArrayList<>();
    private String javaExecutable;
    private File wrapperFile;

    public JavaApplication run(File jarFile, RemoteLogger handler) {
        return null;
    }

    public String getJavaExecutable() {
        return javaExecutable;
    }

    public RemoteLogger getRemoteLogger() {
        return remoteLogger;
    }

    public Collection<JavaApplication> getRunningApps() {
        return Collections.unmodifiableCollection(runningApps);
    }
}
