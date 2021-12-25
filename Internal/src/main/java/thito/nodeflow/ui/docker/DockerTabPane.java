package thito.nodeflow.ui.docker;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import thito.nodeflow.binding.MappedListBinding;
import thito.nodeflow.task.TaskThread;

public class DockerTabPane extends BorderPane {

    public static final PseudoClass HIGHLIGHTED = PseudoClass.getPseudoClass("highlighted");
    private ObjectProperty<DockerPosition> headerPosition = new SimpleObjectProperty<>();
    private DockerContext context;
    private ObservableList<DockerTab> tabList = TaskThread.UI().watch(FXCollections.observableArrayList());
    private ObjectProperty<DockerTab> focusedTab = new SimpleObjectProperty<>();
    private ObjectProperty<Runnable> checkAutoClose = new SimpleObjectProperty<>();
    private DockerNodeContainer container = new DockerNodeContainer();

    private BooleanProperty headerVisible = new SimpleBooleanProperty(true);

    private Pane containerHighlighter = new Pane();
    private Pane flowHeader = new Pane(), header;
    private DoubleProperty scrollOffset = new SimpleDoubleProperty();
    private MappedListBinding binding;

    public DockerTabPane(DockerContext context) {
        getStyleClass().add("docker-tab-pane");
        container.getStyleClass().add("docker-tab-pane-node-container");
        containerHighlighter.getStyleClass().add("docker-tab-pane-node-container-highlighter");
        flowHeader.getStyleClass().add("docker-tab-pane-header");
        this.context = context;
        containerHighlighter.setMouseTransparent(true);
        minHeightProperty().bind(
                Bindings.when(headerPositionProperty().isEqualTo(DockerPosition.LEFT).or(headerPositionProperty().isEqualTo(DockerPosition.RIGHT)))
                        .then(30).otherwise(Bindings.when(headerVisible).then(flowHeader.heightProperty()).otherwise(0)));
        minWidthProperty().bind(
                Bindings.when(headerPositionProperty().isEqualTo(DockerPosition.LEFT).or(headerPositionProperty().isEqualTo(DockerPosition.RIGHT)))
                        .then(Bindings.when(headerVisible).then(flowHeader.widthProperty()).otherwise(0)).otherwise(30));
        setCenter(new StackPane(container, containerHighlighter));
//        centerProperty().bind(Bindings.when(Bindings.isEmpty(tabList)).then((Node) null).otherwise(new StackPane(container, containerHighlighter)));
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(widthProperty());
        clip.heightProperty().bind(heightProperty());
        flowHeader.setClip(clip);
        headerVisible.addListener((obs, old, val) -> {
            TaskThread.UI().schedule(() -> {
                if (val) {
                    setHeaderPosition(headerPosition.get(), flowHeader);
                } else {
                    setHeaderPosition(null, null);
                }
            });
        });
        flowHeader.addEventHandler(ScrollEvent.SCROLL, event -> {
            double size;
            if (headerPosition.get() == DockerPosition.LEFT || headerPosition.get() == DockerPosition.RIGHT) {
                size = header.getHeight() - flowHeader.getHeight();
            } else {
                size = header.getWidth() - flowHeader.getWidth();
            }
            double incremental = event.getTextDeltaY() * 8;
            scrollOffset.set(Math.min(0, Math.max(-size, scrollOffset.get() + incremental)));
        });
        headerPosition.addListener((obs, old, val) -> {
            if (old != null) {
                setHeaderPosition(old, null);
            }
            if (val != null) {
                for (Node n : flowHeader.getChildren()) {
                    if (n instanceof Pane) {
                        ((Pane) n).getChildren().clear();
                    }
                }
                if (binding != null) binding.unbind();
                if (val == DockerPosition.LEFT || val == DockerPosition.RIGHT) {
                    header = new VBox();
                    header.minHeightProperty().bind(flowHeader.heightProperty());
                    header.layoutYProperty().bind(scrollOffset);
                } else {
                    header = new HBox();
                    header.minWidthProperty().bind(flowHeader.widthProperty());
                    header.layoutXProperty().bind(scrollOffset);
                }
                header.layoutBoundsProperty().addListener(obs1 -> refreshOffset());
                flowHeader.getChildren().setAll(header);
                setHeaderPosition(val, flowHeader);
                header.getStyleClass().add("docker-tab-pane-header");
                binding = MappedListBinding.bind(header.getChildren(), tabList, tab -> {
                    tab.paneProperty().set(this);
                    tab.getHeader().headerPositionProperty().bind(headerPosition);
                    if (focusedTab.get() == null) {
                        focusedTab.set(tab);
                    }
                    return tab.getHeader();
                });
            }
        });
        tabList.addListener((ListChangeListener<? super DockerTab>) c -> {
            while (c.next()) {
                if (c.wasRemoved()) {
                    for (DockerTab removed : c.getRemoved()) {
                        if (focusedTab.get() == removed) {
                            focusedTab.set(null);
                        }
                        removed.paneProperty().set(null);
                    }
                    if (focusedTab.get() == null) {
                        if (!tabList.isEmpty()) {
                            focusedTab.set(tabList.get(tabList.size() - 1));
                        } else {
                            focusedTab.set(null);
                        }
                    }
                }
            }
        });
        focusedTab.addListener((obs, old, val) -> {
            if (old != null) {
                old.pseudoClassStateChanged(DockerTab.SELECTED, false);
                old.getHeader().pseudoClassStateChanged(DockerTab.SELECTED, false);
                old.tabFocusedProperty().set(false);
            }
            if (val != null) {
                val.pseudoClassStateChanged(DockerTab.SELECTED, true);
                val.getHeader().pseudoClassStateChanged(DockerTab.SELECTED, true);
                container.setCenter(val);
                val.tabFocusedProperty().set(true);
                scrollToFocus(val);
            } else {
                container.setCenter(null);
            }
        });
        flowHeader.addEventHandler(DragEvent.DRAG_OVER, event -> {
            if (!tabList.isEmpty()) return;
            Dragboard dragboard = event.getDragboard();
            Object object = dragboard.getContent(DockerContext.DOCKER_DRAG);
            if (isContexted(object)) {
                event.acceptTransferModes(TransferMode.MOVE);
                event.consume();
            }
        });
        flowHeader.addEventHandler(DragEvent.DRAG_ENTERED, event -> {
            if (!tabList.isEmpty()) return;
            Dragboard dragboard = event.getDragboard();
            Object object = dragboard.getContent(DockerContext.DOCKER_DRAG);
            if (isContexted(object)) {
                pseudoClassStateChanged(HIGHLIGHTED, true);
                event.consume();
            }
        });
        flowHeader.addEventHandler(DragEvent.DRAG_EXITED, event -> {
            if (!tabList.isEmpty()) return;
            Dragboard dragboard = event.getDragboard();
            Object object = dragboard.getContent(DockerContext.DOCKER_DRAG);
            if (isContexted(object)) {
                pseudoClassStateChanged(HIGHLIGHTED, false);
                event.consume();
            }
        });
        flowHeader.addEventHandler(DragEvent.DRAG_DROPPED, event -> {
            if (!tabList.isEmpty()) return;
            Dragboard dragboard = event.getDragboard();
            Object object = dragboard.getContent(DockerContext.DOCKER_DRAG);
            if (isContexted(object)) {
                pseudoClassStateChanged(HIGHLIGHTED, false);
                event.setDropCompleted(true);
                event.consume();
                DockerTab tab = getContext().getDrag();
                if (tab == null) return;
                getContext().setDrag(null);
                tabList.add(tab);
                focusedTab.set(tab);
            }
        });
        container.addEventHandler(DragEvent.DRAG_OVER, event -> {
            Dragboard dragboard = event.getDragboard();
            Object object = dragboard.getContent(DockerContext.DOCKER_DRAG);
            if (object instanceof DockerDrag) {
                event.acceptTransferModes(TransferMode.MOVE);
                event.consume();
            }
        });
        container.addEventHandler(DragEvent.DRAG_ENTERED, event -> {
            Dragboard dragboard = event.getDragboard();
            Object object = dragboard.getContent(DockerContext.DOCKER_DRAG);
            if (isContexted(object)) {
                pseudoClassStateChanged(HIGHLIGHTED, true);
                event.consume();
            }
        });
        container.addEventHandler(DragEvent.DRAG_EXITED, event -> {
            Dragboard dragboard = event.getDragboard();
            Object object = dragboard.getContent(DockerContext.DOCKER_DRAG);
            if (isContexted(object)) {
                pseudoClassStateChanged(HIGHLIGHTED, false);
                event.consume();
            }
        });
        container.addEventHandler(DragEvent.DRAG_DROPPED, event -> {
            Dragboard dragboard = event.getDragboard();
            Object object = dragboard.getContent(DockerContext.DOCKER_DRAG);
            if (isContexted(object)) {
                pseudoClassStateChanged(HIGHLIGHTED, false);
                event.setDropCompleted(true);
                event.consume();
                DockerTab tab = getContext().getDrag();
                if (tab == null) return;
                getContext().setDrag(null);
                tabList.add(tab);
                focusedTab.set(tab);
            }
        });
        flowHeader.layoutBoundsProperty().addListener(obs -> refreshOffset());
        headerPosition.set(DockerPosition.TOP);
    }

    public void scrollToFocus(DockerTab dockerTab) {
        DockerTab.DockerTabHeader header = dockerTab.getHeader();
        Bounds layoutBounds = header.getLayoutBounds();
        if (layoutBounds != null && dockerTab.paneProperty().get() == this) {
            double offset = scrollOffset.get();
            if (headerPosition.get() == DockerPosition.TOP || headerPosition.get() == DockerPosition.BOTTOM) {
                double length = this.header.getWidth();
                double visibleLength = flowHeader.getWidth();
                double minX = layoutBounds.getMinX();
                double maxX = layoutBounds.getMaxX();
                // visibleMinX = offset
                double visibleMaxX = offset + visibleLength;
                if (minX < offset) {
                    offset = minX;
                } else if (maxX > visibleMaxX) {
                    offset += visibleLength - maxX;
                }
            } else {
                double length = this.header.getHeight();
                double visibleLength = flowHeader.getHeight();
                double minY = layoutBounds.getMinY();
                double maxY = layoutBounds.getMaxY();
            }
            scrollOffset.set(offset);
        }
    }

    public ObjectProperty<Runnable> checkAutoCloseProperty() {
        return checkAutoClose;
    }

    private void refreshOffset() {
        double size;
        if (headerPosition.get() == DockerPosition.LEFT || headerPosition.get() == DockerPosition.RIGHT) {
            size = header.getHeight() - flowHeader.getHeight();
        } else {
            size = header.getWidth() - flowHeader.getWidth();
        }
        scrollOffset.set(Math.min(0, Math.max(-size, scrollOffset.get())));
    }

    private boolean isContexted(Object object) {
        return object instanceof DockerDrag && ((DockerDrag) object).contextId.equals(getContext().getRuntimeId());
    }
    public DockerNodeContainer getContainer() {
        return container;
    }

    public BooleanProperty headerVisibleProperty() {
        return headerVisible;
    }

    public ObjectProperty<DockerTab> focusedTabProperty() {
        return focusedTab;
    }

    public ObservableList<DockerTab> getTabList() {
        return tabList;
    }

    public ObjectProperty<DockerPosition> headerPositionProperty() {
        return headerPosition;
    }

    protected void setHeaderPosition(DockerPosition position, Pane node) {
        if (position == null || !headerVisible.get()) {
            setLeft(null);
            setRight(null);
            setTop(null);
            setBottom(null);
            return;
        }
        switch (position) {
            case LEFT -> setLeft(node);
            case RIGHT -> setRight(node);
            case TOP -> setTop(node);
            case BOTTOM -> setBottom(node);
        }
        pseudoClassStateChanged(DockerTab.TOP, position == DockerPosition.TOP);
        pseudoClassStateChanged(DockerTab.BOTTOM, position == DockerPosition.BOTTOM);
        pseudoClassStateChanged(DockerTab.LEFT, position == DockerPosition.LEFT);
        pseudoClassStateChanged(DockerTab.RIGHT, position == DockerPosition.RIGHT);
    }

    public DockerContext getContext() {
        return context;
    }

    private class DockerNodeContainer extends BorderPane {
        public DockerNodeContainer() {
            getStyleClass().add("docker-node-container");
        }
    }

}
