package thito.nodeflow.ui;

import javafx.beans.*;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.css.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.stage.*;
import javafx.util.*;
import thito.nodeflow.binding.MappedListBinding;
import thito.nodeflow.task.ScheduledTask;
import thito.nodeflow.task.TaskThread;
import thito.nodeflow.util.Toolkit;

import java.util.*;
import java.util.stream.*;

public class LayoutDebugger {

    private Window window;
    private Stage stage;

    private Pane highlightLayer = new Pane();
    private Pane highlight = new Pane();
    private Line vLine1 = new Line();
    private Line vLine2 = new Line();
    private Line hLine1 = new Line();
    private Line hLine2 = new Line();

    private BorderPane pane = new BorderPane();
    private TextField search = new TextField();
    private ObservableList<String> list = FXCollections.observableArrayList();
    private ObservableList<String> filtered = FXCollections.observableArrayList();
    private ListView<String> informationList = new ListView<>(filtered);
    private TreeView<Node> hierarchy = new TreeView<>();

    private InvalidationListener updateHierarchy = obs -> updateHierarchy();

    private ObjectProperty<Node> selection = new SimpleObjectProperty<>();
    private BooleanProperty lockObject = new SimpleBooleanProperty();

    private ScheduledTask runningTask;

    public LayoutDebugger(Window window) {
        this.window = window;
        initStage();
    }

    protected void initStage() {
        highlightLayer.setMouseTransparent(true);
//        highlightLayer.setManaged(false);
        highlightLayer.getChildren().addAll(highlight, vLine1, vLine2, hLine1, hLine2);

        stage = new Stage();
        stage.setTitle("F7 to lock object");

        Stage owner = window.getStage();
        stage.initOwner(owner);
        stage.initStyle(StageStyle.UTILITY);
        stage.initModality(Modality.NONE);
        stage.setScene(new Scene(pane));
        owner.xProperty().addListener((obs, old, val) -> {
            stage.setX(val.doubleValue() + owner.getWidth());
        });
        owner.widthProperty().addListener((obs, old, val) -> {
            stage.setX(owner.getX() + val.doubleValue());
        });
        owner.yProperty().addListener((obs, old, val) -> {
            stage.setY(val.doubleValue());
        });
        owner.heightProperty().addListener((obs, old, val) -> {
            stage.setHeight(val.doubleValue());
        });
        stage.setWidth(400);

        initializeLayout();

        highlightLayer.visibleProperty().addListener((obs, old, val) -> {
            if (runningTask != null) {
                stage.hide();
                runningTask.cancel();
            }
            if (val) {
                stage.show();
                runningTask = TaskThread.UI().schedule(this::updateSelection, Duration.millis(16), Duration.millis(16));
            }
        });

        highlight.setBackground(new Background(new BackgroundFill(Color.color(1, 1, 0, 0.5), null, null)));
        vLine1.setStroke(Color.color(1, 1, 0, 0.5));
        vLine1.setStrokeWidth(1.5);
        vLine1.setStrokeType(StrokeType.CENTERED);
        vLine1.getStrokeDashArray().add(5d);
        vLine1.setStrokeDashOffset(5);

        bindTo(vLine2, vLine1);
        bindTo(hLine1, vLine1);
        bindTo(hLine2, vLine1);

        selection.addListener((obs, old, val) -> {
            list.clear();
            if (val != null) {
                TreeItem<Node> item = find(hierarchy.getRoot(), val);
                if (item != null) {
                    hierarchy.getSelectionModel().select(item);
                }
                list.add("Type: "+val.getClass().getName());
                list.add("Id: "+val.getId());
                list.add("Classes: "+val.getStyleClass());
                list.add("Pseudo Classes: "+val.getPseudoClassStates());
                list.add("Layout X: "+val.getLayoutX());
                list.add("Layout Y: "+val.getLayoutY());
                list.add("CSS Selector: "+val.getTypeSelector());
                if (val instanceof Region) {
                    list.add("Width: "+((Region) val).getWidth());
                    list.add("Height: "+((Region) val).getHeight());
                    list.add("Min Width: "+((Region) val).getMinWidth());
                    list.add("Min Height: "+((Region) val).getMinHeight());
                    list.add("Max Width: "+((Region) val).getMaxWidth());
                    list.add("Max Height: "+((Region) val).getMaxHeight());
                    list.add("Pref Width: "+((Region) val).getPrefWidth());
                    list.add("Pref Height: "+((Region) val).getPrefHeight());
                }
                if (val instanceof ImageView) {
                    Image image = ((ImageView) val).getImage();
                    if (image != null) {
                        list.add("Image: "+image.getUrl());
                        if (image.getException() != null) {
                            list.add(image.getException().toString());
                        }
                    } else {
                        list.add("Image: NONE");
                    }
                }
                addMetaData(val, val.getCssMetaData());
                list.add("CSS:");
                if (val instanceof Parent) {
                    list.addAll(((Parent) val).getStylesheets());
                }
            }
        });

//        lockObject.addListener((obs, old, val) -> {
//            if (val) {
//
//            }
//        });

        search.textProperty().addListener((obs, old, val) -> {
            updateFilteredList();
        });
        list.addListener((InvalidationListener) observable -> updateFilteredList());

        initHierarchyListener();
    }

    static String toStringElement(Object o) {
        if (o instanceof Background) {
            return ((Background) o).getFills().stream().map(x -> x.getFill()+" "+x.getInsets()+" "+x.getRadii()).collect(Collectors.toList()) +
                    " "+((Background) o).getImages().stream().map(x -> x.getImage()+" "+x.getPosition()+" "+x.getRepeatX()+" "+x.getRepeatY()+" "+x.getSize()).collect(Collectors.toList())+" "+((Background) o).getOutsets();
        }
        return String.valueOf(o);
    }

    protected TreeItem<Node> find(TreeItem<Node> root, Node node) {
        if (root.getValue() == node) return root;
        for (TreeItem<Node> child : root.getChildren()) {
            TreeItem<Node> found = find(child, node);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    protected void initHierarchyListener() {
        hierarchy.setCellFactory(new Callback<>() {
            @Override
            public TreeCell<Node> call(TreeView<Node> nodeTreeView) {
                return new TreeCell<>() {

                    {
                        addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
                            if (isEmpty()) {
                                getTreeView().getSelectionModel().clearSelection();
                                lockObject.set(false);
                            } else {
                                lockObject.set(true);
                                selection.set(getTreeItem().getValue());
                                getTreeView().getSelectionModel().select(getTreeItem());
                            }
                        });
                    }

                    @Override
                    protected void updateItem(Node node, boolean b) {
                        super.updateItem(node, b);
                        if (!b) {
                            setText(properName(node));
                        } else {
                            setText(null);
                        }
                    }
                };
            }
        });
        window.skinProperty().addListener((obs, old, val) -> {
            if (old != null) {
                old.getChildrenUnmodifiable().addListener(updateHierarchy);
            }
            if (val != null) {
                val.getChildrenUnmodifiable().removeListener(updateHierarchy);
            }
            updateHierarchy();
        });
        stage.showingProperty().addListener((obs, old, val) -> {
            if (val) {
                updateHierarchy();
            } else {
                hierarchy.setRoot(null);
            }
        });
        updateHierarchy();
    }

    protected void updateHierarchy() {
        if (!window.stage.isShowing()) return;
        hierarchy.setRoot(new NodeItem(window.getSkin()));
    }

    void updateFilteredList() {
        if (Toolkit.isNullOrEmpty(search.getText())) {
            filtered.setAll(list);
        } else {
            List<String> target = new ArrayList<>(list.size());
            Map<String, Double> searchValue = new HashMap<>();
            for (String t : list) {
                double value = searchValue.computeIfAbsent(t, s -> Toolkit.searchScore(s, search.getText()));
                if (value > 0) target.add(t);
            }
            filtered.retainAll(target);
            target.removeAll(filtered);
            filtered.addAll(target);
            filtered.sort(Comparator.<String>comparingDouble(x -> searchValue.computeIfAbsent(x, s -> Toolkit.searchScore(s, search.getText()))).reversed());
        }
    }

    void bindTo(Line a, Line b) {
        a.strokeProperty().bind(b.strokeProperty());
        Bindings.bindContent(a.getStrokeDashArray(), b.getStrokeDashArray());
        a.strokeWidthProperty().bind(b.strokeWidthProperty());
        a.strokeDashOffsetProperty().bind(b.strokeDashOffsetProperty());
        a.strokeTypeProperty().bind(b.strokeTypeProperty());
    }

    void addMetaData(Node node, List<CssMetaData<? extends Styleable, ?>> metaDataList) {
        if (metaDataList == null) return;
        for (CssMetaData meta : metaDataList) {
            String text = meta.getProperty();
            StyleableProperty styleableProperty = meta.getStyleableProperty(node);
            if (styleableProperty != null) {
                list.add(text + ": " + toStringElement(styleableProperty.getValue()));
            }

            addMetaData(node, meta.getSubProperties());
        }
    }

    protected void initializeLayout() {
        ScrollPane node = new ScrollPane(informationList);
        node.setPannable(true);
        node.setFillToWidth(true);
        node.setFillToHeight(true);
        search.setPromptText("Search");
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(search);
        borderPane.setCenter(node);
        ScrollPane scrollPane = new ScrollPane(hierarchy);
        scrollPane.setFillToHeight(true);
        scrollPane.setFillToWidth(true);
        SplitPane splitPane = new SplitPane(borderPane, scrollPane);
        splitPane.setOrientation(Orientation.HORIZONTAL);
        pane.setCenter(splitPane);
    }

    public boolean isLockObject() {
        return lockObject.get();
    }

    public BooleanProperty lockObjectProperty() {
        return lockObject;
    }

    public Pane getHighlightLayer() {
        return highlightLayer;
    }

    public BooleanProperty visibleProperty() {
        return highlightLayer.visibleProperty();
    }

    Node findNode(Node parent) {
        Point2D mouse = Toolkit.mouse();
        if (parent instanceof Parent) {
            for (Node child : ((Parent) parent).getChildrenUnmodifiable()) {
                Bounds screenBounds = child.localToScreen(child.getLayoutBounds());
                if (screenBounds.contains(mouse)) {
                    return findNode(child);
                }
            }
        }
        Bounds screenBounds = parent.localToScreen(parent.getLayoutBounds());
        if (screenBounds != null && screenBounds.contains(mouse)) {
            return parent;
        }
        return null;
    }

    public void updateSelection() {
        if (!isLockObject()) {
            selection.set(findNode(window.skinProperty().get()));
        }

        Node node = selection.get();

        if (node == null || !highlightLayer.isVisible()) {
            highlight.setVisible(false);
            vLine1.setVisible(false);
            vLine2.setVisible(false);
            hLine1.setVisible(false);
            hLine2.setVisible(false);
            return;
        }

        highlight.setVisible(true);
        vLine1.setVisible(true);
        vLine2.setVisible(true);
        hLine1.setVisible(true);
        hLine2.setVisible(true);

        Bounds local = highlightLayer.sceneToLocal(node.localToScene(node.getLayoutBounds()));

        highlight.setLayoutX(local.getMinX());
        highlight.setLayoutY(local.getMinY());
        highlight.setPrefSize(local.getWidth(), local.getHeight());

        vLine1.setStartX(local.getMinX());
        vLine1.setStartY(0);
        vLine1.setEndX(local.getMinX());
        vLine1.setEndY(highlightLayer.getHeight());

        vLine2.setStartX(local.getMaxX());
        vLine2.setStartY(0);
        vLine2.setEndX(local.getMaxX());
        vLine2.setEndY(highlightLayer.getHeight());

        hLine1.setStartX(0);
        hLine1.setStartY(local.getMinY());
        hLine1.setEndX(highlightLayer.getWidth());
        hLine1.setEndY(local.getMinY());

        hLine2.setStartX(0);
        hLine2.setStartY(local.getMaxY());
        hLine2.setEndX(highlightLayer.getWidth());
        hLine2.setEndY(local.getMaxY());
    }

    public static String properName(Node node) {
        if (node == null) return "null";
        String id = node.getId();
        return id == null ? node.getClass().getSimpleName() : id;
    }

    public class NodeItem extends TreeItem<Node> {
        private Node node;

        public NodeItem(Node node) {
            super(node);
            this.node = node;
            if (node instanceof SplitPane) {
                MappedListBinding.bind(getChildren(), ((SplitPane) node).getItems(), NodeItem::new);
            } else if (node instanceof Parent) {
                MappedListBinding.bind(getChildren(), ((Parent) node).getChildrenUnmodifiable(), NodeItem::new);
            }
        }

        public Node getNode() {
            return node;
        }
    }
}
