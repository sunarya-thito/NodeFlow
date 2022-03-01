package thito.nodeflow.project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import thito.nodeflow.project.module.FileModule;
import thito.nodeflow.resource.Resource;
import thito.nodeflow.task.TaskThread;
import thito.nodeflow.task.batch.Batch;
import thito.nodeflow.ui.dashboard.DashboardWindow;
import thito.nodeflow.ui.editor.EditorWindow;
import thito.nodeflow.ui.editor.docker.FileViewerComponent;

import java.util.ArrayList;
import java.util.List;

public class ProjectManager {
    private static ProjectManager instance = new ProjectManager();

    public static ProjectManager getInstance() {
        return instance;
    }

    private FileViewerComponent fileViewerComponent = new FileViewerComponent();
    private ObservableSet<ProjectContext> activeProjects = TaskThread.BG().watch(FXCollections.observableSet());
    private List<FileModule> moduleList = new ArrayList<>();

    public List<FileModule> getModuleList() {
        return moduleList;
    }

    public ObservableSet<ProjectContext> getActiveProjects() {
        return activeProjects;
    }

    public FileViewerComponent getFileViewerComponent() {
        return fileViewerComponent;
    }

    public Batch.Task closeProject(ProjectContext context) {
        return Batch.execute(TaskThread.IO(), p -> {
            Resource bin = context.getProject().getDirectory().getChild("editor.bin");
            p.append(TaskThread.BG(), context.saveEditorData(bin));
            p.append(TaskThread.BG(), progress -> {
                List<EditorWindow> editorWindows = new ArrayList<>(context.getActiveWindows());
                progress.insert(TaskThread.UI(), px -> {
                    px.setStatus("Closing active editor windows");
                    for (EditorWindow editorWindow : editorWindows) {
                        editorWindow.forceClose();
                    }
                });
            }).append(TaskThread.BG(), progress -> {
                activeProjects.remove(context);
                if (activeProjects.isEmpty()) {
                    progress.insert(TaskThread.UI(), pd -> {
                        DashboardWindow.getWindow().show();
                    });
                }
            });
        });
    }

    public Batch.Task openProject(ProjectProperties projectProperties) {
        return Batch.execute(TaskThread.BG(), p -> {
            for (ProjectContext open : activeProjects) {
                if (open.getProject().getProperties().equals(projectProperties)) {
                    p.insert(TaskThread.BG(), pr -> {
                        for (EditorWindow editorWindow : open.getActiveWindows()) {
                            TaskThread.UI().schedule(() -> {
                                editorWindow.getStage().requestFocus();
                            });
                        }
                    });
                    return;
                }
            }
            p.append(TaskThread.IO(), pr2 -> {
                Project project = new Project(projectProperties.getWorkspace(), projectProperties);
                pr2.append(TaskThread.BG(), pr -> {
                    pr.setStatus("Initializing project context");
                    ProjectContext projectContext = new ProjectContext(project);
                    pr.append(TaskThread.BG(), progress -> {
                        progress.setStatus("Initializing project handlers");
//                        for (List<ProjectHandlerFactory> factory : PluginManager.getPluginManager().getProjectHandlerMap().values()) {
//                            for (ProjectHandlerFactory f : factory) {
//                                progress.insert(TaskThread.BG(), handler -> {
//                                    projectContext.getProjectHandlers().add(f.createHandler(projectContext));
//                                });
//                            }
//                        }
                    });
                    pr2.append(TaskThread.IO(), px -> {
                        Resource editorBin = projectContext.getProject().getDirectory().getChild("editor.bin");
                        px.append(projectContext.loadEditorData(editorBin));
                        px.append(TaskThread.BG(), progress -> {
                            activeProjects.add(projectContext);
                        });
                    });
                });
            });
        });
    }

}
