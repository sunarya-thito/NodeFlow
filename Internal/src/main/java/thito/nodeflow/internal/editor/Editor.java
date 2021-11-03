package thito.nodeflow.internal.editor;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import thito.nodeflow.config.Section;
import thito.nodeflow.internal.annotation.BGThread;
import thito.nodeflow.internal.editor.content.EditorContentType;
import thito.nodeflow.internal.editor.state.EditorState;
import thito.nodeflow.internal.plugin.PluginManager;
import thito.nodeflow.internal.plugin.ProjectHandler;
import thito.nodeflow.internal.plugin.ProjectHandlerRegistry;
import thito.nodeflow.internal.project.Project;
import thito.nodeflow.internal.project.ProjectProperties;
import thito.nodeflow.internal.resource.Resource;
import thito.nodeflow.internal.task.ProgressedTask;
import thito.nodeflow.internal.task.TaskThread;
import thito.nodeflow.internal.ui.editor.EditorWindow;

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

    @BGThread
    public void loadProject(ProjectProperties projectProperties) {
        if (!editorWindow.getStage().isShowing()) throw new IllegalStateException("headless editor");
        Project project = new Project(projectProperties.getWorkspace(), projectProperties);
        Section handlerSection = projectProperties.getConfiguration().getOrCreateMap("handlers");
        CompletableFuture<EditorState> editorState = new CompletableFuture<>();
        editorWindow.submitTask(new ProgressedTask("Loading editor state", () -> {
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
        }));
        for (ProjectHandlerRegistry registry : PluginManager.getPluginManager().getProjectHandlerRegistryList()) {
            editorWindow.submitTask(new ProgressedTask("Loading "+registry.getId(), () -> {
                ProjectHandler handler = registry.loadHandler(project, handlerSection);
                project.getProjectHandlers().add(handler);
            }));
        }
        editorWindow.submitTask(new ProgressedTask("Loading project", () -> {
            this.project.set(project);
        }));
    }

    @BGThread
    public void closeProject() {

    }

    public ReadOnlyObjectProperty<Project> projectProperty() {
        return project;
    }
}
