package thito.nodeflow.ui.editor.docker;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import thito.nodeflow.language.I18n;
import thito.nodeflow.project.ProjectContext;
import thito.nodeflow.ui.docker.DockNode;
import thito.nodeflow.ui.docker.DockNodeState;
import thito.nodeflow.ui.docker.DockerComponent;
import thito.nodeflow.ui.docker.DockerPosition;
import thito.nodeflow.ui.editor.EditorWelcomeSkin;

import java.io.Serial;

public class WelcomePageComponent implements DockerComponent {
    @Override
    public I18n displayName() {
        return I18n.$("editor.welcome.tab-title");
    }

    @Override
    public boolean allowMultipleView() {
        return false;
    }

    @Override
    public boolean isMenuAccessible() {
        return false;
    }

    @Override
    public boolean isDefaultComponent() {
        return true;
    }

    @Override
    public DockerPosition getDefaultPosition() {
        return DockerPosition.TOP;
    }

    @Override
    public DockNode createDockNode(ProjectContext projectContext, DockNodeState dockNodeState) {
        return DockNodeState.check(dockNodeState, State.class) ? new Node() : null;
    }

    public static class State implements DockNodeState {
        @Serial
        private static final long serialVersionUID = 1L;
    }

    public class Node extends DockNode {

        private SimpleObjectProperty<Image> image = new SimpleObjectProperty<>();
        private EditorWelcomeSkin welcomeSkin;

        public Node() {
            welcomeSkin = new EditorWelcomeSkin();
            setCenter(welcomeSkin);
        }

        @Override
        public DockerComponent getComponent() {
            return WelcomePageComponent.this;
        }

        @Override
        public I18n titleProperty() {
            return I18n.$("editor.welcome.tab-title");
        }

        @Override
        public ObjectProperty<Image> iconProperty() {
            return image;
        }

        @Override
        public DockNodeState createState() {
            return new State();
        }
    }
}
