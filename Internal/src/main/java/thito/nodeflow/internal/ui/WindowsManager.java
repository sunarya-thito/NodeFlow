package thito.nodeflow.internal.ui;

import thito.nodeflow.internal.ui.launcher.*;
import thito.nodeflow.internal.ui.resourcemonitor.*;
import thito.nodeflow.internal.ui.settings.*;

public class WindowsManager {
    private LauncherWindowImpl launcher;
    private SettingsWindow settings;
    private ResourceMonitorWindow resourceMonitor;

    public void initializeWindows() {
        if (launcher == null && settings == null && resourceMonitor == null) {
            launcher = new LauncherWindowImpl();
            settings = new SettingsWindow();
            resourceMonitor = new ResourceMonitorWindow();
        }
    }

    public ResourceMonitorWindow getResourceMonitor() {
        initializeWindows();
        return resourceMonitor;
    }

    public LauncherWindowImpl getLauncher() {
        initializeWindows();
        return launcher;
    }

    public SettingsWindow getSettings() {
        initializeWindows();
        return settings;
    }
}
