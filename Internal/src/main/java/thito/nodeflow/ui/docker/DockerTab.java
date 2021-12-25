package thito.nodeflow.ui.docker;

import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import thito.nodeflow.task.TaskThread;
import thito.nodeflow.util.Toolkit;

import java.util.function.Supplier;

public class DockerTab extends StackPane {

    public static final PseudoClass SELECTED = PseudoClass.getPseudoClass("selected");
    public static final PseudoClass TOP = PseudoClass.getPseudoClass("top");
    public static final PseudoClass BOTTOM = PseudoClass.getPseudoClass("bottom");
    public static final PseudoClass LEFT = PseudoClass.getPseudoClass("left");
    public static final PseudoClass RIGHT = PseudoClass.getPseudoClass("right");
    private BooleanProperty tabFocused = new SimpleBooleanProperty();
    private ObjectProperty<DockerTabPane> pane = new SimpleObjectProperty<>();
    private ObjectProperty<EventHandler<ActionEvent>> onClosed;
    private BooleanProperty closable = new SimpleBooleanProperty(true);
    private DockerTabHeader header = new DockerTabHeader();
    private StringProperty title;
    private ObjectProperty<Image> icon;
    private DockerContext context;
    private ObjectProperty<Node> content = new SimpleObjectProperty<>();

    {
        contentProperty().addListener((obs, old, val) -> {
            if (old instanceof DockNode) {
                ((DockNode) old).tabFocusedProperty().unbind();
            }
            if (old != null) {
                getChildren().remove(old);
            }
            if (val instanceof DockNode) {
                ((DockNode) val).tabFocusedProperty().bind(tabFocused);
            }
            if (val != null) {
                getChildren().add(val);
            }
        });
    }

    public DockerTab(DockerContext context) {
        this.context = context;
        getStyleClass().add("docker-tab");
    }

    public DockerTab(DockerContext context, DockNode node) {
        node.tabProperty().set(this);
        this.context = context;
        titleProperty().bind(node.titleProperty());
        iconProperty().bind(node.iconProperty());
        onClosedProperty().set(event -> {
            node.onCloseAttempt(result -> {
                if (result) {
                    DockerTabPane dockerTabPane = paneProperty().get();
                    if (dockerTabPane != null) {
                        dockerTabPane.getTabList().remove(this);
                    }
                }
            });
        });
        contentProperty().set(node);
    }

    public BooleanProperty tabFocusedProperty() {
        return tabFocused;
    }

    public StringProperty titleProperty() {
        return title;
    }

    public ObjectProperty<EventHandler<ActionEvent>> onClosedProperty() {
        return onClosed;
    }

    public ObjectProperty<Image> iconProperty() {
        return icon;
    }

    public ObjectProperty<DockerTabPane> paneProperty() {
        return pane;
    }

    public BooleanProperty closableProperty() {
        return closable;
    }

    public DockerTabHeader getHeader() {
        return header;
    }

    public ObjectProperty<Node> contentProperty() {
        return content;
    }

    public DockerContext getContext() {
        return context;
    }

    public class DockerTabHeader extends BorderPane {
        private ObjectProperty<DockerPosition> headerPosition = new SimpleObjectProperty<>();
        private ImageView icon = new ImageView();
        private Label title = new Label();
        private BorderPane titleWrap = new BorderPane(new Group(title));
        private Button closeButton = new Button();

        private Pane highlighterView = new Pane();
        private DockerTabPane previousParent;

        private boolean isContexted(Object object) {
            return object instanceof DockerDrag && ((DockerDrag) object).contextId.equals(getContext().getRuntimeId());
        }
        public double getSize() {
            return headerPosition.get() == DockerPosition.LEFT || headerPosition.get() == DockerPosition.RIGHT ?
                    getHeight() : getWidth();
        }

        private void clearSurrounding() {
            setLeft(null);
            setRight(null);
            setBottom(null);
            setTop(null);
        }
        public DockerTabHeader() {
            HBox.setHgrow(this, Priority.ALWAYS);
            VBox.setVgrow(this, Priority.ALWAYS);
            onClosed = closeButton.onActionProperty();
            DockerTab.this.title = title.textProperty();
            DockerTab.this.icon = icon.imageProperty();
            getStyleClass().add("docker-tab-header");
            highlighterView.getStyleClass().add("docker-tab-header-highlighter-view");
            icon.getStyleClass().add("docker-tab-header-icon");
            title.getStyleClass().add("docker-tab-header-title");
            closeButton.getStyleClass().add("docker-tab-header-close-button");
            VBox.setVgrow(titleWrap, Priority.ALWAYS);
            HBox.setHgrow(titleWrap, Priority.ALWAYS);
            headerPosition.addListener(this::update);
            closable.addListener(this::update);

            setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

            addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
                event.consume();
                DockerTabPane pane = paneProperty().get();
                if (pane != null) {
                    pane.focusedTabProperty().set(DockerTab.this);
                }
            });
            addEventHandler(MouseEvent.DRAG_DETECTED, event -> {
                previousParent = pane.get();
                WritableImage screenShotHighlight = new WritableImage((int) getWidth(), (int) getHeight());
                snapshot(null, screenShotHighlight);
                Dragboard dragboard = startDragAndDrop(TransferMode.MOVE);
                dragboard.setDragView(screenShotHighlight, event.getX(), event.getY());
                ClipboardContent content = new ClipboardContent();
                DockerTabPane p = pane.get();
                getContext().setDrag(DockerTab.this);
                content.put(DockerContext.DOCKER_DRAG, new DockerDrag(getContext().getRuntimeId()));
                dragboard.setContent(content);
                p.getTabList().remove(DockerTab.this);
                event.consume();
            });
            addEventHandler(DragEvent.DRAG_OVER, event -> {
                Dragboard dragboard = event.getDragboard();
                Object object = dragboard.getContent(DockerContext.DOCKER_DRAG);
                if (isContexted(object)) {
                    event.acceptTransferModes(TransferMode.MOVE);
                    event.consume();
                    if (getLeft() == highlighterView || getTop() == highlighterView) {
                        if (headerPosition.get() == DockerPosition.LEFT || headerPosition.get() == DockerPosition.RIGHT) {
                            if (event.getY() >= getHeight() / 4 * 3) {
                                clearSurrounding();
                                setBottom(highlighterView);
                            }
                        } else {
                            if (event.getX() >= getWidth() / 4 * 3) {
                                clearSurrounding();
                                setRight(highlighterView);
                            }
                        }
                    } else {
                        if (headerPosition.get() == DockerPosition.LEFT || headerPosition.get() == DockerPosition.RIGHT) {
                            if (event.getY() < getHeight() / 4) {
                                clearSurrounding();
                                setTop(highlighterView);
                            }
                        } else {
                            if (event.getX() < getWidth() / 4) {
                                clearSurrounding();
                                setLeft(highlighterView);
                            }
                        }
                    }
                }
            });
            addEventHandler(DragEvent.DRAG_ENTERED, event -> {
                Dragboard dragboard = event.getDragboard();
                Object object = dragboard.getContent(DockerContext.DOCKER_DRAG);
                if (isContexted(object)) {
                    clearSurrounding();
                    dragboard.setDragView(null);
                    if (headerPosition.get() == DockerPosition.LEFT || headerPosition.get() == DockerPosition.RIGHT) {
                        highlighterView.setPrefSize(-1, Math.max(pane.get().getHeight() / (pane.get().getTabList().size() + 1), getCenter().prefHeight(-1)));
                        if (event.getY() >= getHeight() / 2) {
                            setBottom(highlighterView);
                        } else {
                            setTop(highlighterView);
                        }
                    } else {
                        highlighterView.setPrefSize(Math.max(pane.get().getWidth() / (pane.get().getTabList().size() + 1), getCenter().prefWidth(-1)), -1);
                        if (event.getX() >= getWidth() / 2) {
                            setRight(highlighterView);
                        } else {
                            setLeft(highlighterView);
                        }
                    }
                    event.consume();
                }
            });
            addEventHandler(DragEvent.DRAG_EXITED, event -> {
                Dragboard dragboard = event.getDragboard();
                Object object = dragboard.getContent(DockerContext.DOCKER_DRAG);
                if (isContexted(object)) {
                    clearSurrounding();
                    event.consume();
                }
            });
            addEventHandler(DragEvent.DRAG_DROPPED, event -> {
                Dragboard dragboard = event.getDragboard();
                Object object = dragboard.getContent(DockerContext.DOCKER_DRAG);
                if (isContexted(object)) {
                    event.setDropCompleted(true);
                    DockerTab dragged = getContext().getDrag();
                    getContext().setDrag(null);
                    if (dragged == null) return;
                    int index = Math.max(0, pane.get().getTabList().indexOf(DockerTab.this));
                    if (headerPosition.get() == DockerPosition.LEFT || headerPosition.get() == DockerPosition.RIGHT) {
                        if (event.getY() >= getHeight() / 2) {
                            index++;
                        }
                    } else {
                        if (event.getX() >= getWidth() / 2) {
                            index++;
                        }
                    }
                    pane.get().focusedTabProperty().set(dragged);
                    pane.get().getTabList().add(Math.max(Math.min(index, pane.get().getTabList().size()), 0), dragged);
                    event.consume();
                }
            });
            addEventHandler(DragEvent.DRAG_DONE, event -> {
                getContext().setDrag(null);
                if (!event.isAccepted()) {
                    Supplier<DockerWindow> dockerWindowSupplier = getContext().getDockerWindowSupplier();
                    if (dockerWindowSupplier != null) {
                        DockerWindow dockerWindow = dockerWindowSupplier.get();
                        dockerWindow.getDockerPane().getCenterTabs().getTabList().add(DockerTab.this);
                        dockerWindow.show();
                        TaskThread.UI().schedule(() -> {
                            Point2D mouse = Toolkit.mouse();
                            dockerWindow.setPosition(mouse.getX(), mouse.getY());
                        });
                    }
                }
                DockerTabPane previousParent = this.previousParent;
                TaskThread.UI().schedule(() -> {
                    checkAutoClose(previousParent);
                });
                this.previousParent = null;
            });
        }

        private static void checkAutoClose(DockerTabPane dockerTabPane) {
            if (dockerTabPane != null) {
                Runnable runnable = dockerTabPane.checkAutoCloseProperty().get();
                if (runnable != null) {
                    runnable.run();
                }
            }
        }

        public ObjectProperty<DockerPosition> headerPositionProperty() {
            return headerPosition;
        }

        void update(Observable observable) {
            switch (headerPosition.get()) {
                case LEFT -> {
                    VBox box = new VBox();
                    box.setFillWidth(true);
                    if (closable.get()) {
                        box.getChildren().add(closeButton);
                    }
                    box.setAlignment(Pos.CENTER);
                    title.setRotate(-90);
                    box.getChildren().addAll(titleWrap, icon);
                    setCenter(box);
                }
                case RIGHT -> {
                    VBox box = new VBox();
                    box.setFillWidth(true);
                    box.setAlignment(Pos.CENTER);
                    box.getChildren().addAll(icon, titleWrap);
                    title.setRotate(90);
                    if (closable.get()) {
                        box.getChildren().add(closeButton);
                    }
                    setCenter(box);
                }
                case TOP, BOTTOM -> {
                    HBox box = new HBox();
                    box.setFillHeight(true);
                    box.setAlignment(Pos.CENTER);
                    title.setRotate(0);
                    box.getChildren().addAll(icon, titleWrap);
                    if (closable.get()) {
                        box.getChildren().add(closeButton);
                    }
                    setCenter(box);
                }
            }
            DockerPosition position = headerPosition.get();
            pseudoClassStateChanged(TOP, position == DockerPosition.TOP);
            pseudoClassStateChanged(BOTTOM, position == DockerPosition.BOTTOM);
            pseudoClassStateChanged(LEFT, position == DockerPosition.LEFT);
            pseudoClassStateChanged(RIGHT, position == DockerPosition.RIGHT);
            getCenter().getStyleClass().add("docker-tab-header-wrapper");
        }
    }
}
