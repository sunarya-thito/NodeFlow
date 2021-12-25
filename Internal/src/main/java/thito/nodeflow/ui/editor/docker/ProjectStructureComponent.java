package thito.nodeflow.ui.editor.docker;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import thito.nodeflow.language.I18n;
import thito.nodeflow.project.ProjectContext;
import thito.nodeflow.resource.Resource;
import thito.nodeflow.ui.docker.DockNode;
import thito.nodeflow.ui.docker.DockNodeState;
import thito.nodeflow.ui.docker.DockerComponent;
import thito.nodeflow.ui.docker.DockerPosition;
import thito.nodeflow.ui.editor.EditorFilePanelSkin;

import java.io.Serial;
import java.util.HashSet;
import java.util.Set;

public class ProjectStructureComponent implements DockerComponent {
    @Override
    public I18n displayName() {
        return I18n.$("editor.navigation.project-files");
    }

    @Override
    public boolean allowMultipleView() {
        return false;
    }

    @Override
    public boolean isMenuAccessible() {
        return true;
    }

    @Override
    public boolean isDefaultComponent() {
        return true;
    }

    @Override
    public DockerPosition getDefaultPosition() {
        return DockerPosition.LEFT;
    }

    @Override
    public DockNode createDockNode(ProjectContext projectContext, DockNodeState dockNodeState) {
        return DockNodeState.check(dockNodeState, State.class) ? new Node(projectContext, (State) dockNodeState) : null;
    }

    public static class State implements DockNodeState {
        @Serial
        private static final long serialVersionUID = 1L;

        public Set<String> expandedPaths = new HashSet<>();
    }

    public class Node extends DockNode {
        private I18n title = I18n.$("editor.navigation.project-files");
        private ObjectProperty<Image> icon = new SimpleObjectProperty<>();
        private EditorFilePanelSkin filePanelSkin;

        public Node(ProjectContext projectContext, State dockNodeState) {
            filePanelSkin = new EditorFilePanelSkin(projectContext);
            filePanelSkin.getExplorerView().setTaskQueue(projectContext.getTaskQueue());
            setCenter(filePanelSkin);
            if (dockNodeState != null) {
                if (dockNodeState.expandedPaths != null) {
                    filePanelSkin.getExpandedPaths().addAll(dockNodeState.expandedPaths);
                }
            }
        }

        @Override
        public DockerComponent getComponent() {
            return ProjectStructureComponent.this;
        }

        @Override
        public I18n titleProperty() {
            return title;
        }

        @Override
        public ObjectProperty<Image> iconProperty() {
            return icon;
        }

        @Override
        public DockNodeState createState() {
            State state = new State();
            addExpanded(state, filePanelSkin.getExplorerView().getRoot());
            return state;
        }

        private void addExpanded(State state, TreeItem<Resource> parent) {
            if (parent.isExpanded()) {
                Resource resource = parent.getValue();
                String path = resource.getPath();
                state.expandedPaths.add(path);
            }
            for (TreeItem<Resource> child : parent.getChildren()) {
                addExpanded(state, child);
            }
        }
    }

}
