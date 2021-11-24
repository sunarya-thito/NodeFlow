package thito.nodeflow.editor;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import thito.nodeflow.config.Section;
import thito.nodeflow.annotation.BGThread;
import thito.nodeflow.editor.content_legacy.EditorContentType;
import thito.nodeflow.editor.state.EditorState;
import thito.nodeflow.plugin.PluginManager;
import thito.nodeflow.project.Project;
import thito.nodeflow.project.ProjectProperties;
import thito.nodeflow.resource.Resource;
import thito.nodeflow.task.BatchTask;
import thito.nodeflow.task.TaskThread;
import thito.nodeflow.ui.editor.EditorWindow;

import java.io.ObjectInputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Editor {
    private EditorWindow editorWindow;
    private ObjectProperty<Project> project = TaskThread.BG().lock(new SimpleObjectProperty<>());

    @BGThread
    public Editor() {
        editorWindow = new EditorWindow(this);
    }

    public void loadProject(ProjectProperties projectProperties) {
        if (!editorWindow.getStage().isShowing()) throw new IllegalStateException("headless editor");
        closeProject();
        Project project = new Project(projectProperties.getWorkspace(), projectProperties);
        Section handlerSection = projectProperties.getConfiguration().getOrCreateMap("handlers");
        CompletableFuture<EditorState> editorState = new CompletableFuture<>();
        BatchTask batchTask = new BatchTask();
        batchTask.submitTask(progress -> {
            TaskThread.IO().schedule(() -> {
                Resource resource = project.getDirectory().getChild("editor.bin");
                if (resource.exists()) {
                    try (ObjectInputStream objectInputStream = new ObjectInputStream(resource.openInput())) {
                        editorState.complete((EditorState) objectInputStream.readObject());
                    } catch (Throwable t) {
                        t.printStackTrace();
                        editorState.complete(new EditorState());
                    }
                } else {
                    editorState.complete(new EditorState());
                }
            });
            try {
                EditorState state = editorState.get();
                state.dockingPositionMap.forEach((key, value) -> {
                    EditorContentType factory = EditorManager.getEditorElement(key);
                    if (factory != null) {
                        editorWindow.getSkin().getEditorContent().dock(factory.createElement(this).getNode(), value);
                    }
                });
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
//        for (ProjectHandlerRegistry registry : PluginManager.getPluginManager().getProjectHandlerRegistryList()) {
//            batchTask.submitTask(progress -> {
//                progress.setStatus("Loading "+registry.getId());
//                ProjectHandler handler = registry.loadHandler(project, handlerSection);
//                project.getProjectHandlers().add(handler);
//            });
//        }
        batchTask.submitTask(progress -> {
            this.project.set(project);
        });
        editorWindow.runBatchTask(batchTask);
    }

    public void closeProject() {
        BatchTask batchTask = new BatchTask();
    }

    public ReadOnlyObjectProperty<Project> projectProperty() {
        return project;
    }
}
