package thito.nodeflow.api.ui.launcher.page;

import thito.nodeflow.api.Update;
import thito.nodeflow.api.ui.launcher.LauncherPage;

import java.util.List;

public interface UpdatesPage extends LauncherPage {

    void refreshUpdates();

    List<Update> getUpdates();

}
