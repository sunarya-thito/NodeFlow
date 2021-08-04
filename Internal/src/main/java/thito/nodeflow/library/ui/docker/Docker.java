package thito.nodeflow.library.ui.docker;

import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.collections.*;
import javafx.css.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.library.*;

import java.util.*;

public class Docker extends AnchorPane {
    private static boolean isParentAncestorOfNode(Parent parent, Node node) {
        if (parent == null || node == null || parent == node || node.getParent() == null) {
            return false;
        }

        Parent current = node.getParent();
        do {
            if (current.equals(parent)) {
                return true;
            }
            current = current.getParent();
        } while (current != null);
        return false;
    }
    /**
     * Special field to detect the current dragged container
     */
    static DockerContainer draggingContainer;
    static DockerContainer focus;

    private static final CssMetaData<Docker, Pos> positionCss = new CssMetaData<Docker, Pos>("-fx-docker-position", (StyleConverter<?, Pos>) StyleConverter.getEnumConverter(Pos.class)) {
        @Override
        public boolean isSettable(Docker styleable) {
            return !styleable.relativePosition.isBound();
        }

        @Override
        public StyleableProperty<Pos> getStyleableProperty(Docker styleable) {
            return styleable.relativePosition;
        }
    };

    private static final CssMetaData<Docker, Orientation> orientationCss = new CssMetaData<Docker, Orientation>("-fx-docker-orientation", (StyleConverter<?, Orientation>) StyleConverter.getEnumConverter(Orientation.class)) {
        @Override
        public boolean isSettable(Docker styleable) {
            return !styleable.orientation.isBound();
        }

        @Override
        public StyleableProperty<Orientation> getStyleableProperty(Docker styleable) {
            return styleable.orientation;
        }
    };

    private Object dockerType;
    private ObservableList<DockerContainer> dockerContainers = FXCollections.observableArrayList();
    protected Pane flowDocker;
    private ViewportDocker viewportDocker = new ViewportDocker();
    private StyleableObjectProperty<Pos> relativePosition = new SimpleStyleableObjectProperty<>(positionCss, Pos.TOP_LEFT);
    private StyleableObjectProperty<Orientation> orientation = new SimpleStyleableObjectProperty<>(orientationCss, Orientation.HORIZONTAL);
    private BooleanProperty highlightedOpen = new SimpleBooleanProperty();
    private BooleanProperty disableHide = new SimpleBooleanProperty();

    private DockerContainer.DockerSnapshot snapshot;
    private Pane currentParent;

    private ChangeListener<? super Node> focusListener = (obs, old, val) -> {
        if (val == null) {
            for (int i = dockerContainers.size() - 1; i >= 0; i--) {
                dockerContainers.get(i).getTab().focusProperty().set(false);
            }
            return;
        }
        for (int i = dockerContainers.size() - 1; i >= 0; i--) {
            DockerContainer container = dockerContainers.get(i);
            if (container.getViewport() instanceof Parent) {
                container.getTab().focusProperty().set(isParentAncestorOfNode((Parent) container.getViewport(), val));
            }
        }
    };

    public Docker() {
        Toolkit.style(this, "docker");
        sceneProperty().addListener((obs, old, val) -> {
            if (old != null) {
                old.focusOwnerProperty().removeListener(focusListener);
            }
            if (val != null) {
                val.focusOwnerProperty().addListener(focusListener);
            }
        });
        dockerContainers.addListener((ListChangeListener<DockerContainer>) change -> {
            while (change.next()) {
                for (DockerContainer added : change.getAddedSubList()) {
                    if (added.getDocker() != null) throw new IllegalStateException("multiple parent");
                    added.setParent(this);
                    added.getTab().updateOrientation(orientationProperty().get(), relativePosition.get());
                    added.getTab().highlightedOpenProperty().bindBidirectional(highlightedOpenProperty());
                    flowDocker.getChildren().add(added.getTab());
                }
                for (DockerContainer removed : change.getRemoved()) {
                    removed.getTab().highlightedOpenProperty().unbindBidirectional(highlightedOpenProperty());
                    removed.setParent(null);
                    flowDocker.getChildren().remove(removed.getTab());
                }
            }
        });
        disableHide.addListener((obs, old, val) -> {
            if (val) {
                if (dockerContainers.size() > 0 && viewportDocker.selected.get() == null) {
                    open(dockerContainers.get(0));
                }
            }
        });

        heightProperty().addListener(x -> updateOverflow());
        widthProperty().addListener(x -> updateOverflow());

        relativePosition.addListener(x -> updateRotation());
        orientationProperty().addListener(x -> updateRotation());

        updateRotation();

        addEventHandler(DragEvent.DRAG_OVER, event -> {
            if (draggingContainer != null) {
                draggingContainer.updateSnapshot(this);
                event.acceptTransferModes(TransferMode.MOVE);
                int index = flowDocker.getChildren().indexOf(snapshot);
                int newIndex = getIndexByScreen(event.getSceneX(), event.getSceneY());
                if (index != newIndex && snapshot != null) {
                    try {
                        flowDocker.getChildren().remove(snapshot);
                        flowDocker.getChildren().add(newIndex, snapshot);
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        });

        addEventHandler(DragEvent.DRAG_ENTERED, event -> {
            if (draggingContainer != null && snapshot == null) {
                int index = getIndexByScreen(event.getSceneX(), event.getSceneY());
                draggingContainer.updateSnapshot(this);
                flowDocker.getChildren().add(index, snapshot = draggingContainer.getSnapshot());
            }
        });

        addEventHandler(DragEvent.DRAG_EXITED, event -> {
            flowDocker.getChildren().remove(snapshot);
            snapshot = null;
        });

        addEventHandler(DragEvent.DRAG_DROPPED, event -> {
            if (draggingContainer != null) {
                event.setDropCompleted(true);
                if (isAcceptedType(draggingContainer.getDockerType())) {
                    int index = flowDocker.getChildren().indexOf(snapshot);
                    if (index >= 0) {
                        snapshot.setIndex(index);
                        try {
                            Docker parent = draggingContainer.getDocker();
                            if (parent != this) {
                                parent.getContainers().remove(draggingContainer);
                                getContainers().add(draggingContainer);
                                flowDocker.getChildren().remove(draggingContainer.getTab());
                            }
                            flowDocker.getChildren().set(index, draggingContainer.getTab());
                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                    }
                }
                return;
            }
            event.setDropCompleted(false);
        });
    }

    public ObjectProperty<Pos> relativePositionProperty() {
        return relativePosition;
    }

    private void updateOverflow() {
    }

    public void updateSnapshot(DockerContainer container) {
        int index = flowDocker.getChildren().indexOf(container.getTab());
        snapshot = container.getSnapshot();
        if (index >= 0) {
            flowDocker.getChildren().set(index, snapshot);
        }
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return new ExtraList<>(super.getCssMetaData(), orientationCss, positionCss);
    }

    public BooleanProperty highlightedOpenProperty() {
        return highlightedOpen;
    }

    public Object getDockerType() {
        return dockerType;
    }

    public void setDockerType(Object dockerType) {
        this.dockerType = dockerType;
    }

    public boolean isAcceptedType(Object dockerType) {
        return Objects.equals(dockerType, this.dockerType);
    }

    private void updateRotation() {
        if (currentParent != null) {
            currentParent.getChildren().clear();
            getChildren().remove(currentParent);
        }
        if (orientationProperty().get() == Orientation.VERTICAL) {
            if (relativePosition.get().getHpos() == HPos.LEFT) {
                Pane old = flowDocker;
                currentParent = new HBox(flowDocker = new VBox(), viewportDocker);
                if (old != null) {
                    flowDocker.getChildren().setAll(old.getChildren());
                }
                setRightAnchor(currentParent, 0d);
            } else {
                Pane old = flowDocker;
                currentParent = new HBox(viewportDocker, flowDocker = new VBox());
                if (old != null) {
                    flowDocker.getChildren().setAll(old.getChildren());
                }
                setLeftAnchor(currentParent, 0d);
            }
            setTopAnchor(currentParent, 0d);
            setBottomAnchor(currentParent, 0d);
        } else {
            if (relativePosition.get().getVpos() == VPos.BOTTOM) {
                Pane old = flowDocker;
                currentParent = new VBox(viewportDocker, flowDocker = new HBox());
                if (old != null) {
                    flowDocker.getChildren().setAll(old.getChildren());
                }
                setTopAnchor(currentParent, 0d);
            } else {
                Pane old = flowDocker;
                currentParent = new VBox(flowDocker = new HBox(), viewportDocker);
                if (old != null) {
                    flowDocker.getChildren().setAll(old.getChildren());
                }
                setBottomAnchor(currentParent, 0d);
            }
            setLeftAnchor(currentParent, 0d);
            setRightAnchor(currentParent, 0d);
        }
        getChildren().add(currentParent);
        Toolkit.style(flowDocker, "docker-tabs");
        for (int i = dockerContainers.size() - 1; i >= 0; i--) {
            dockerContainers.get(i).getTab().updateOrientation(orientationProperty().get(), relativePosition.get());
        }
    }

    public BooleanProperty disableHideProperty() {
        return disableHide;
    }

    public void hide() {
        if (disableHide.get()) return;
        open(null);
    }

    public boolean isOpen(DockerContainer dockerContainer) {
        return viewportDocker.selected.get() == dockerContainer;
    }

    public void open(DockerContainer dockerContainer) {
        if (focus != null) {
            focus.getTab().focusProperty().set(false);
        }
        focus = dockerContainer;
        if (dockerContainer != null) {
            dockerContainer.getTab().focusProperty().set(true);
        }
        viewportDocker.selected.set(dockerContainer);
    }

    public ObjectProperty<Orientation> orientationProperty() {
        return orientation;
    }

    public ObservableList<DockerContainer> getContainers() {
        return dockerContainers;
    }

    public int getIndexByScreen(double x, double y) {
        ObservableList<Node> children = flowDocker.getChildren();
        for (int i = children.size() - 1; i >= 0; i--) {
            Node child = children.get(i);
            if (child.getBoundsInLocal().contains(child.sceneToLocal(x, y))) {
                return i;
            }
        }
        return Math.max(0, children.size() - 1);
    }

    public class ViewportDocker extends BorderPane {
        private ObjectProperty<DockerContainer> selected = new SimpleObjectProperty<>();

        public ViewportDocker() {
            selected.addListener((obs, old, val) -> {
                if (old != null) {
                    old.getTab().selectedProperty().set(false);
                }
                if (val == null) {
                    setCenter(null);
                } else {
                    val.getTab().selectedProperty().set(true);
                    setCenter(val.getViewport());
                }
            });
            addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
                if (isResizing(event.getX(), event.getY())) {
//                    System.out.println("resizing!");
//                    setCursor(orientationProperty().get() == Orientation.HORIZONTAL ? Cursor.S_RESIZE : Cursor.N_RESIZE);
                } else {
//                    setCursor(Cursor.DEFAULT);
                }
            });
        }

        private boolean isResizing(double x, double y) {
            if (orientationProperty().get() == Orientation.VERTICAL) {
                if (relativePositionProperty().get().getHpos() == HPos.LEFT) {
                    System.out.println(x+" >= "+(getWidth() - 5));
                    return x >= getWidth() - DockerContext.CONTROL_LENGTH;
                } else {
                    System.out.println(x+" <= "+(5));
                    return x <= DockerContext.CONTROL_LENGTH;
                }
            } else {
                if (relativePositionProperty().get().getVpos() == VPos.BOTTOM) {
                    System.out.println(y+"y <= "+(5));
                    return y <= DockerContext.CONTROL_LENGTH;
                } else {
                    System.out.println(y+"y >= "+(getHeight() - 5));
                    return y >= getHeight() - DockerContext.CONTROL_LENGTH;
                }
            }
        }
    }
}
