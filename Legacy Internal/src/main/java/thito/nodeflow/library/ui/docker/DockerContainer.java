package thito.nodeflow.library.ui.docker;

import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.stage.*;

public class DockerContainer extends BorderPane {
    private Docker parent;
    private DockerTab dockerTab = new DockerTab(this);
    private Object dockerType;
    private DockerSnapshot snapshot = new DockerSnapshot();
    private Window previousOwner;

    protected void updateSnapshot(Docker parent) {
        if (parent != null) {
            if (parent.orientationProperty().get() == Orientation.HORIZONTAL) {
                if (dockerTab.parentOrientation == Orientation.HORIZONTAL) {
                    snapshot.setMinWidth(dockerTab.getWidth());
                    snapshot.setMinHeight(dockerTab.getHeight());
                } else {
                    snapshot.setMinWidth(dockerTab.getHeight());
                    snapshot.setMinHeight(dockerTab.getWidth());
                }
            } else {
                if (dockerTab.parentOrientation == Orientation.VERTICAL) {
                    snapshot.setMinWidth(dockerTab.getWidth());
                    snapshot.setMinHeight(dockerTab.getHeight());
                } else {
                    snapshot.setMinWidth(dockerTab.getHeight());
                    snapshot.setMinHeight(dockerTab.getWidth());
                }
            }
        }
    }

    protected Window getPreviousOwner() {
        return previousOwner;
    }

    protected void setPreviousOwner(Window previousOwner) {
        if (previousOwner != null)
            this.previousOwner = previousOwner;
    }

    public DockerSnapshot getSnapshot() {
        return snapshot;
    }

    protected void setParent(Docker parent) {
        if (parent != this.parent && this.parent != null) {
            if (this.parent.isOpen(this)) {
                this.parent.hide();
            }
        }
        if (parent != null) {
            Scene scene = parent.getScene();
            if (scene != null) {
                setPreviousOwner(scene.getWindow());
            }
        }
        this.parent = parent;
        if (getTab().wasOpened) {
            this.parent.open(this);
        }
    }

    public Object getDockerType() {
        return dockerType;
    }

    public void setDockerType(Object dockerType) {
        this.dockerType = dockerType;
    }

    public Node getViewport() {
        return getCenter();
    }

    public void setViewport(Node viewport) {
        boolean wasSelected = false;
        if (parent != null && parent.isOpen(this)) {
            wasSelected = true;
        }
        setCenter(viewport);
        if (parent != null && wasSelected) {
            parent.open(this);
        }
    }

    public DockerTab getTab() {
        return dockerTab;
    }

    public Docker getDocker() {
        return parent;
    }

    public static class DockerSnapshot extends Pane {
        private Docker docker;
        private int index;

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
//            System.out.println("setting index at "+index);
        }

        public void removeParent() {
            Parent parent = getParent();
            if (parent != null) {
                ((Pane) parent).getChildren().remove(this);
            }
        }

        public Docker getDocker() {
            return docker;
        }

        public void setDocker(Docker docker) {
            this.docker = docker;
        }
    }
}
