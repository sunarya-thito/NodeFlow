package thito.nodeflow.ui.docker;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import thito.nodeflow.annotation.BGThread;
import thito.nodeflow.annotation.UIThread;
import thito.nodeflow.project.ProjectContext;
import thito.nodeflow.task.TaskThread;

import java.lang.ref.WeakReference;

public class DockerManager {
    private static DockerManager manager = new DockerManager();

    public static DockerManager getManager() {
        return manager;
    }


    private final ObservableList<DockerComponent> dockerComponentSet = TaskThread.BG().watch(FXCollections.observableArrayList());

    private DockerManager() {}

    @BGThread
    public void registerDockerComponent(DockerComponent dockerComponent) {
        synchronized (dockerComponentSet) {
            dockerComponentSet.add(dockerComponent);
        }
    }

    @BGThread
    public void unregisterDockerComponent(DockerComponent dockerComponent) {
        synchronized (dockerComponentSet) {
            dockerComponentSet.remove(dockerComponent);
        }
    }

    public ObservableList<DockerComponent> getDockerComponentList() {
        return dockerComponentSet;
    }

    @UIThread
    public DockNode createDockNode(ProjectContext projectContext, DockNodeState dockNodeState) {
        synchronized (dockerComponentSet) {
            for (DockerComponent dockerComponent : dockerComponentSet) {
                DockNode dockNode = dockerComponent.createDockNode(projectContext, dockNodeState);
                if (dockNode != null) {
                    return dockNode;
                }
            }
        }
        return null;
    }
}
