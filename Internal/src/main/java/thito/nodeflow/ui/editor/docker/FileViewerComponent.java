package thito.nodeflow.ui.editor.docker;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.Image;
import thito.nodeflow.language.I18n;
import thito.nodeflow.project.ProjectContext;
import thito.nodeflow.project.module.FileModule;
import thito.nodeflow.resource.Resource;
import thito.nodeflow.resource.ResourceType;
import thito.nodeflow.task.TaskThread;
import thito.nodeflow.ui.docker.*;
import thito.nodeflow.ui.editor.FileViewerSkin;

import java.io.File;
import java.util.function.Consumer;

public class FileViewerComponent implements DockerComponent {

    @Override
    public I18n displayName() {
        return null;
    }

    @Override
    public boolean allowMultipleView() {
        return true;
    }

    @Override
    public boolean isMenuAccessible() {
        return false;
    }

    @Override
    public boolean isDefaultComponent() {
        return false;
    }

    @Override
    public DockerPosition getDefaultPosition() {
        return DockerPosition.TOP;
    }

    @Override
    public DockNode createDockNode(ProjectContext projectContext, DockNodeState dockNodeState) {
        if (DockNodeState.check(dockNodeState, State.class)) {
            State state = (State) dockNodeState;
            if (state != null) {
                File file = state.file;
                if (file != null) {
                    return new Node(projectContext, file);
                }
            }
        }
        return null;
    }

    public DockNode createDockNode(ProjectContext context, Resource resource) {
        return new Node(context, resource);
    }

    public static class State implements DockNodeState {
        private File file;

        public State(File file) {
            this.file = file;
        }
    }

    public class Node extends DockNode {
        private File file;
        private Resource resource;
        private FileViewerSkin fileViewerSkin;
        private ProjectContext context;
        private ObjectProperty<Image> icon = new SimpleObjectProperty<>();

        public Node(ProjectContext context, Resource resource) {
            this.file = resource.toFile();
            this.resource = resource;
            this.context = context;
            initialize();
        }
        public Node(ProjectContext context, File resource) {
            this.file = resource;
            this.context = context;
            TaskThread.IO().schedule(() -> {
                this.resource = context.getProject().getWorkspace().getResourceManager().getResource(resource);
                initialize();
            });
        }

        @Override
        public void onCloseAttempt(Consumer<Boolean> resultConsumer) {
            fileViewerSkin.close();
            super.onCloseAttempt(resultConsumer);
        }

        void initialize() {
            resource.typeProperty().addListener(new ChangeListener<>() {
                @Override
                public void changed(ObservableValue<? extends ResourceType> observableValue, ResourceType resourceType, ResourceType val) {
                    if (val != ResourceType.FILE) {
                        TaskThread.UI().schedule(() -> {
                            DockerTab dockerTab = tabProperty().get();
                            if (dockerTab != null) {
                                DockerTabPane dockerTabPane = dockerTab.paneProperty().get();
                                if (dockerTabPane != null) {
                                    dockerTabPane.getTabList().remove(dockerTab);
                                }
                            }
                        });
                        resource.typeProperty().removeListener(this);
                    }
                }
            });
            TaskThread.BG().schedule(() -> {
//                FileModule fileModule = PluginManager.getPluginManager().getModule(resource);
//                TaskThread.UI().schedule(() -> {
//                    icon.bind(fileModule.iconProperty());
//                    this.fileViewerSkin = new FileViewerSkin(resource, fileModule, context);
//                    setCenter(fileViewerSkin);
//                });
            });
        }

        public FileViewerSkin getFileViewerSkin() {
            return fileViewerSkin;
        }

        public ProjectContext getContext() {
            return context;
        }

        public Resource getResource() {
            return resource;
        }

        @Override
        public I18n titleProperty() {
            return I18n.direct(file.getName());
        }

        @Override
        public ObjectProperty<Image> iconProperty() {
            return icon;
        }

        @Override
        public DockNodeState createState() {
            return new State(resource.toFile());
        }

        @Override
        public DockerComponent getComponent() {
            return FileViewerComponent.this;
        }
    }
}
