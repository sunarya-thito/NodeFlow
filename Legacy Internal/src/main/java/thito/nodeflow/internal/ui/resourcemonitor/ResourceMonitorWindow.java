package thito.nodeflow.internal.ui.resourcemonitor;

import javafx.application.*;
import javafx.collections.*;
import thito.nodeflow.internal.ui.*;

public class ResourceMonitorWindow extends WindowImpl {

    public static void push(String task) {
//        ResourceMonitorWindow window = UIManagerImpl.getInstance().getWindowsManager().getResourceMonitor();
//        if (window != null) {
//            window.pushTask(task);
//        }
    }

    private final ObservableList<String> historyTask = FXCollections.observableArrayList();

    public ResourceMonitorWindow() {
        getStage().setHeight(500);
        getStage().setWidth(600);
        getStage().setMinHeight(500);
        getStage().setMinWidth(600);
        getStage().setTitle("Resource Monitor");
        getMenu().getItems().add(requestDefaultApplicationMenu());
        getMenu().getItems().add(requestDefaultWindowMenu());
        getMenu().getItems().add(requestDefaultHelpMenu());
    }

    public void pushTask(String task) {
        Platform.runLater(() -> { // can't do it using internal task manager
            historyTask.add(task);
            if (historyTask.size() > 300) {
                historyTask.remove(0);
            }
        });
    }

    public ObservableList<String> getHistoryTask() {
        return historyTask;
    }

    @Override
    protected void initializeViewport() {
        setViewport(new ResourceMonitorUI(this));
    }

    @Override
    public String getName() {
        return "ResourceMonitorWindow";
    }

}
