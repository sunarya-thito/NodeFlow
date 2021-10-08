package thito.nodeflow.internal.ui;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableBooleanProperty;
import javafx.css.StyleConverter;
import javafx.css.StyleableBooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import thito.nodeflow.internal.binding.MappedListBinding;

import java.util.function.Predicate;

public class BetterTreeView extends BorderPane {
    private static final CssMetaData<BetterTreeView, Boolean> fxShowRoot = UIHelper.meta("-fx-show-root", StyleConverter.getBooleanConverter(), false, BetterTreeView::showRootProperty);
    private StyleableBooleanProperty showRoot = new SimpleStyleableBooleanProperty(fxShowRoot, false);
    private ObjectProperty<Item> root = new SimpleObjectProperty<>();
    private ObjectProperty<Predicate<? super Item>> filter = new SimpleObjectProperty<>();

    private ScrollPane scrollPane;
    private VBox viewport;

    public BetterTreeView() {
        initialize();
    }

    private void initialize() {
        viewport = new VBox();
        scrollPane = new ScrollPane(viewport);
        setCenter(scrollPane);
        root.addListener((obs, old, val) -> {

        });
    }

    public StyleableBooleanProperty showRootProperty() {
        return showRoot;
    }

    private static class LinePath extends Pane {
        private Line toTop, toBottom, toRight;

        public LinePath() {
            initialize();
        }

        private void initialize() {
            toTop = new Line();
            toBottom = new Line();
            toRight = new Line();
            toTop.endYProperty().bind(heightProperty().divide(2));
            toBottom.endYProperty().bind(heightProperty().divide(2));
            toRight.endXProperty().bind(widthProperty().divide(2));
            toTop.layoutXProperty().bind(widthProperty().divide(2));
            toBottom.layoutXProperty().bind(widthProperty().divide(2));
            toBottom.layoutYProperty().bind(heightProperty().divide(2));
            toRight.layoutXProperty().bind(widthProperty().divide(2));
            toRight.layoutYProperty().bind(heightProperty().divide(2));
            getChildren().addAll(toTop, toBottom, toRight);
        }
    }

    public static class Item extends BorderPane {
        private ObjectProperty<BetterTreeView> view = new SimpleObjectProperty<>();
        private ObjectProperty<Item> parent = new SimpleObjectProperty<>();
        private ObservableList<Item> items = FXCollections.observableArrayList();
        private FilteredList<Item> children = new FilteredList<>(items);
        private LinePath linePath;
        private BorderPane wrapper;
        private BorderPane viewport;
        private VBox childrenViewport;
        private InvalidationListener indexChangeListener;

        public Item() {
            initialize();
        }

        private void initialize() {
            view.addListener((obs, old, val) -> {
                children.predicateProperty().unbind();
                if (val != null) {
                    children.predicateProperty().bind(val.filter);
                }
            });
            wrapper = new BorderPane();
            viewport = new BorderPane();
            childrenViewport = new VBox();
            BorderPane.setMargin(viewport, new Insets(5, 0, 5, 0));
            linePath = new LinePath();
            indexChangeListener = obs -> {
                Item parent = this.parent.get();
                if (parent != null) {
                    int index = parent.children.indexOf(this);
                    if (index == parent.children.size() - 1) {
                        // last
                        linePath.toBottom.visibleProperty().set(false);
                    } else {
                        linePath.toBottom.visibleProperty().set(true);
                    }
                }
            };
            parent.addListener((obs, old, val) -> {
                if (val == null) {
                    if (old != null) {
                        old.children.removeListener(indexChangeListener);
                    }
                    setLeft(null);
                } else {
                    val.children.addListener(indexChangeListener);
                    indexChangeListener.invalidated(val.children);
                    setLeft(linePath);
                }
            });
            children.addListener((ListChangeListener<? super Item>) c -> {
                while (c.next()) {
                    if (c.wasRemoved()) {
                        for (Item x : c.getRemoved()) {
                            x.parent.set(null);
                        }
                    }
                    if (c.wasAdded()) {
                        for (Item x : c.getAddedSubList()) {
                            x.parent.set(this);
                        }
                    }
                }
            });
            Bindings.bindContent(childrenViewport.getChildren(), children);
            wrapper.setCenter(viewport);
            wrapper.setBottom(childrenViewport);
            setCenter(wrapper);
        }

        public ObservableList<Item> getItems() {
            return items;
        }
    }
}
