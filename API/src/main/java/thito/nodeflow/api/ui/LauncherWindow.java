package thito.nodeflow.api.ui;

import thito.nodeflow.api.ui.launcher.LauncherPage;

import java.util.List;

public interface LauncherWindow extends Window {

    List<LauncherPage> getPages();

}
