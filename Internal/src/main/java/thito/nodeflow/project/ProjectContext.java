package thito.nodeflow.project;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.scene.Node;
import thito.nodeflow.annotation.IOThread;
import thito.nodeflow.project.module.FileViewer;
import thito.nodeflow.resource.Resource;
import thito.nodeflow.task.TaskThread;
import thito.nodeflow.task.batch.Batch;
import thito.nodeflow.task.batch.TaskQueue;
import thito.nodeflow.ui.docker.*;
import thito.nodeflow.ui.editor.EditorWindow;
import thito.nodeflow.ui.editor.EditorWindowState;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Consumer;

public class ProjectContext {
    private Project project;
    private DockerContext dockerContext;
    private ObservableList<EditorWindow> activeWindows = TaskThread.BG().watch(FXCollections.observableArrayList());
    private ObservableSet<Resource> selectedFiles = TaskThread.UI().watch(FXCollections.observableSet(new LinkedHashSet<>()));
    private ObjectProperty<FileViewer> focusedFileViewer = new SimpleObjectProperty<>();
    private ObservableList<ProjectHandler> projectHandlers = FXCollections.observableArrayList();
    private List<EditorWindow> tempClosedWindows;
    private TaskQueue taskQueue = new TaskQueue();

    public ProjectContext(Project project) {
        this.project = project;
        dockerContext = new DockerContext();
        dockerContext.setDockerWindowSupplier(() -> new EditorWindow(this, new DockerPane(dockerContext)));
    }

    public EditorWindow getFocusedWindow() {
        for (EditorWindow window : activeWindows) {
            if (window.getStage().isFocused()) {
                return window;
            }
        }
        return activeWindows.isEmpty() ? null : activeWindows.get(0);
    }

    public ObservableList<ProjectHandler> getProjectHandlers() {
        return projectHandlers;
    }

    public ObjectProperty<FileViewer> focusedFileViewerProperty() {
        return focusedFileViewer;
    }

    private boolean contains(List<DockerTab> tabList, DockerComponent dockerComponent) {
        for (DockerTab d : tabList) {
            Node node = d.contentProperty().get();
            if (node instanceof DockNode && ((DockNode) node).getComponent() == dockerComponent) {
                return true;
            }
        }
        return false;
    }
    public void contains(DockerComponent dockerComponent, Consumer<Boolean> result) {
        Batch.execute(TaskThread.BG(), progress -> {
            for (EditorWindow v : new ArrayList<>(activeWindows)) {
                progress.insertLazy(TaskThread.UI(), p -> {
                    if (contains(v.getDockerPane().getLeftTabs().getTabList(), dockerComponent) ||
                            contains(v.getDockerPane().getCenterTabs().getTabList(), dockerComponent) ||
                            contains(v.getDockerPane().getRightTabs().getTabList(), dockerComponent) ||
                            contains(v.getDockerPane().getBottomTabs().getTabList(), dockerComponent)) {
                        result.accept(true);
                        return;
                    }
                    p.proceed();
                });
            }
            progress.insert(TaskThread.UI(), p -> {
                result.accept(false);
            });
        }).start();
    }

    public TaskQueue getTaskQueue() {
        return taskQueue;
    }

    public ObservableSet<Resource> getSelectedFiles() {
        return selectedFiles;
    }

    protected Batch.Task createDefaultEditor() {
        return Batch.execute(TaskThread.UI(), progress -> {
                    progress.setStatus("Loading default editor");
                    DockerPane dockerPane = new DockerPane(dockerContext);
                    progress.append(Batch.execute(TaskThread.BG(), px -> {
                        for (DockerComponent dockerComponent : DockerManager.getManager().getDockerComponentList()) {
                            px.append(TaskThread.UI(), p -> {
                                if (dockerComponent.isDefaultComponent()) {
                                    DockNode dockNode = dockerComponent.createDockNode(this, null);
                                    if (dockNode != null) {
                                        dockerPane.getTabs(dockerComponent.getDefaultPosition()).getTabList().add(new DockerTab(dockerContext, dockNode));
                                    }
                                }
                            });
                        }
                    }));
                    progress.append(TaskThread.UI(), p -> {
                        createEditorWindow(dockerPane);
                    });
                });
    }

    @IOThread
    protected Batch.Task saveEditorData(Resource resource) {
        return Batch.execute(TaskThread.BG(), progress -> {
            progress.setStatus("Creating editor states");
            List<EditorWindow> tempClosedWindows = this.tempClosedWindows == null ? activeWindows : this.tempClosedWindows;
            this.tempClosedWindows = null;
            progress.append(TaskThread.IO(), p -> {
                p.setStatus("Saving editor states");
                try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(resource.openOutput())) {
                    try {
                        objectOutputStream.writeInt(tempClosedWindows.size());
                        for (EditorWindow window : tempClosedWindows) {
                            DockerPaneState state = window.getDockerPane().saveState();
                            objectOutputStream.writeObject(state);
                            EditorWindowState windowState = new EditorWindowState();
                            windowState.x = window.getStage().getX();
                            windowState.y = window.getStage().getY();
                            windowState.width = window.getStage().getWidth();
                            windowState.height = window.getStage().getHeight();
                            windowState.maximized = window.getStage().isMaximized();
                            windowState.iconified = window.getStage().isIconified();
                            objectOutputStream.writeObject(windowState);
                        }
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            });
        });
    }

    @IOThread
    protected Batch.Task loadEditorData(Resource resource) {
        return Batch.execute(TaskThread.IO(), progress -> {
            progress.setStatus("Loading editor states");
            if (!resource.exists()) {
                progress.append(createDefaultEditor());
                return;
            }
            try (ObjectInputStream objectInputStream = new ObjectInputStream(resource.openInput())) {
                int totalWindows = objectInputStream.readInt();
                for (int i = 0; i < totalWindows; i++) {
                    DockerPaneState state = (DockerPaneState) objectInputStream.readObject();
                    EditorWindowState windowState = (EditorWindowState) objectInputStream.readObject();
                    progress.append(TaskThread.UI(), p -> {
                        p.setStatus("Creating editor window");
                        DockerPane dockerPane = new DockerPane(dockerContext);
                        dockerPane.loadState(this, state);
                        EditorWindow editorWindow = createEditorWindow(dockerPane);
                        progress.append(TaskThread.UI(), p2 -> {
                            editorWindow.getStage().setX(windowState.x);
                            editorWindow.getStage().setY(windowState.y);
                            editorWindow.getStage().setWidth(windowState.width);
                            editorWindow.getStage().setHeight(windowState.height);
                            editorWindow.getStage().setMaximized(windowState.maximized);
                            editorWindow.getStage().setIconified(windowState.iconified);
                        });
                    });
                }
                return;
            } catch (Throwable t) {
                t.printStackTrace();
            }
            progress.append(createDefaultEditor());
        });
    }

    private EditorWindow createEditorWindow(DockerPane dockerPane) {
        EditorWindow editorWindow = new EditorWindow(this, dockerPane);
        editorWindow.progressProperty().bind(taskQueue.progressProperty());
        editorWindow.show();
        return editorWindow;
    }

    public ObservableList<EditorWindow> getActiveWindows() {
        return activeWindows;
    }

    public Project getProject() {
        return project;
    }

    public DockerContext getDockerContext() {
        return dockerContext;
    }

}
