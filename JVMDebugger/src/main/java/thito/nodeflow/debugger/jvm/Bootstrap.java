package thito.nodeflow.debugger.jvm;

import com.sun.management.OperatingSystemMXBean;
import thito.nodeflow.debugger.client.*;

import java.lang.management.*;
import java.lang.reflect.*;
import java.net.*;
import java.rmi.*;
import java.rmi.registry.*;
import java.util.concurrent.*;
import java.util.jar.*;

public class Bootstrap {

    private static Registry registry;
    private static int debuggerPort;
    private static ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    public static void main(String[] args) throws Throwable {
        String port = System.getenv("debuggerPort");
        String jarLocation = System.getenv("jarLocation");

        debuggerPort = Integer.parseInt(port);
        registry = LocateRegistry.getRegistry(debuggerPort);

        service.scheduleAtFixedRate(() -> {
            try {
                ApplicationStatus applicationStatus = new ApplicationStatus();
                Runtime runtime = Runtime.getRuntime();
                applicationStatus.setFreeMemory(runtime.freeMemory());
                applicationStatus.setMaxMemory(runtime.maxMemory());
                applicationStatus.setTotalMemory(runtime.totalMemory());
                applicationStatus.setAvailableProcessors(runtime.availableProcessors());
                OperatingSystemMXBean bean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
                applicationStatus.setCpu(bean.getCpuLoad());
                getRemoteLogger().updateApplicationStatus(applicationStatus);
            } catch (Throwable ignored) {
            }
        }, 1, 1, TimeUnit.SECONDS);

        JarFile jarFile = new JarFile(jarLocation);
        String mainClass = jarFile.getManifest().getMainAttributes().getValue(Attributes.Name.MAIN_CLASS);

        URLClassLoader urlClassLoader = new URLClassLoader(new URL[] { new URL(jarLocation) }, Bootstrap.class.getClassLoader());
        Class<?> main = Class.forName(mainClass, true, urlClassLoader);
        Method method = main.getDeclaredMethod("main", String[].class);
        method.setAccessible(true);
        method.invoke(null, new Object[] { args });
    }

    public static void reportActivity(String threadName, String nodeId, ActivityType type) {
        service.submit(() -> {
            try {
                getRemoteLogger().logActivity(threadName, nodeId, type);
            } catch (NotBoundException | RemoteException ignored) {
            }
        });
    }

    public static RemoteLogger getRemoteLogger() throws NotBoundException, RemoteException {
        return (RemoteLogger) registry.lookup(RemoteLogger.REGISTRY_NAME);
    }

}
