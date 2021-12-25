package thito.nodeflow.settings.application;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import thito.nodeflow.NodeFlow;
import thito.nodeflow.binding.MappedBinding;
import thito.nodeflow.binding.ThreadBinding;
import thito.nodeflow.project.Workspace;
import thito.nodeflow.settings.SettingsCanvas;
import thito.nodeflow.settings.canvas.Category;
import thito.nodeflow.settings.canvas.Item;
import thito.nodeflow.settings.canvas.NumberItem;
import thito.nodeflow.settings.canvas.SettingsContext;
import thito.nodeflow.task.TaskThread;

import java.io.File;

@Category(value = "${settings.general.name}", context = SettingsContext.GLOBAL)
public class General extends SettingsCanvas {

    @Item("${settings.general.items.workspace-directory}")
    public final ObjectProperty<File> workspaceDirectory = new SimpleObjectProperty<>(new File(NodeFlow.ROOT, "Workspace"));

    @Item("${settings.general.items.action-buffer}") @NumberItem(min = 0, max = 150)
    public final ObjectProperty<Integer> actionBuffer = new SimpleObjectProperty<>(100);

    {
        TaskThread.IO().schedule(() -> {
            NodeFlow.getInstance().workspaceProperty().set(new Workspace(workspaceDirectory.get()));
        });
        workspaceDirectory.addListener((obs, old, val) -> {
            TaskThread.IO().schedule(() -> {
                NodeFlow.getInstance().workspaceProperty().set(new Workspace(val));
            });
        });
    }

}