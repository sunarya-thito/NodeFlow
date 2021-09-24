package thito.nodeflow.debugger.client;

import java.rmi.*;

public interface RemoteLogger extends Remote {
    String REGISTRY_NAME = RemoteLogger.class.getName();

    void logActivity(String threadName, String nodeId, ActivityType type);
    void updateApplicationStatus(ApplicationStatus status);

    void writeOut(int b);
    void writeErr(int b);
    int readIn(int b);
}
