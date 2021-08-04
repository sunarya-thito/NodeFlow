package thito.nodeflow.internal.ui.editor;

import javafx.beans.*;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.util.*;
import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.node.*;
import thito.nodeflow.api.project.*;
import thito.nodeflow.api.task.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.internal.node.provider.*;
import thito.nodeflow.internal.node.search.*;
import thito.nodeflow.internal.ui.*;
import thito.nodeflow.library.ui.layout.*;

import java.util.*;

public class NewNodeUI extends UIComponent {

    private ObservableList<NodeProvider> providers = FXCollections.observableArrayList();
    @Component("node-list")
    private ObjectProperty<ListView<NodeProvider>> nodeList = new SimpleObjectProperty<>();

    @Component("category-list")
    private ObjectProperty<ListView<NodeProviderCategory>> categoryList = new SimpleObjectProperty<>();

    @Component("search")
    private ObjectProperty<TextField> search = new SimpleObjectProperty<>();

    @Component("node-preview")
    private ObjectProperty<Pane> preview = new SimpleObjectProperty<>();

    @Component("category-overlay")
    private ObjectProperty<BorderPane> overlayCategory = new SimpleObjectProperty<>();
    @Component("node-overlay")
    private ObjectProperty<BorderPane> overlayNode = new SimpleObjectProperty<>();
    @Component("preview-overlay")
    private ObjectProperty<BorderPane> overlayPreview = new SimpleObjectProperty<>();

    @Component("array-control")
    private ObjectProperty<BorderPane> arrayControl = new SimpleObjectProperty<>();

    @Component("array-dimension-spinner")
    private ObjectProperty<Spinner<Integer>> spinner = new SimpleObjectProperty<>();

    private NewNodeDialogContent content;
    private NodeModule module;
    private boolean onlyEvents, lazyLoad;
    private NodeCategorySearchTool tool;
    private NodeCategorySearchTool tool2;

    public NewNodeUI(Project project, NodeModule module, NewNodeDialogContent content, boolean lazyLoad, boolean onlyEvents) {
        this.lazyLoad = lazyLoad;
        this.onlyEvents = onlyEvents;
        this.module = module;
        this.content = content;
        tool = new NodeCategorySearchTool(project);
        tool2 = new NodeCategorySearchTool(project);
        setLayout(Layout.loadLayout("NewNodeUI"));
    }

    @Override
    protected void onLayoutReady() {
        Pane arrayControlParent = (Pane) arrayControl.get().getParent();
        arrayControlParent.getChildren().remove(arrayControl.get());
        search.get().textProperty().addListener(obs -> updateSearch());
        overlayPreview.get().setMouseTransparent(true);
        preview.get().getChildren().addListener((InvalidationListener) observable -> {
            if (preview.get().getChildren().isEmpty()) {
                overlayPreview.get().setOpacity(1);
            } else {
                overlayPreview.get().setOpacity(0);
            }
        });
        categoryList.get().setCellFactory(new Callback<ListView<NodeProviderCategory>, ListCell<NodeProviderCategory>>() {
            @Override
            public ListCell<NodeProviderCategory> call(ListView<NodeProviderCategory> param) {
                return new ListCell<NodeProviderCategory>() {
                    @Override
                    protected void updateItem(NodeProviderCategory item, boolean empty) {
                        if (item == null) {
                            setGraphic(null);
                        } else {
                            setGraphic(new NodeCategoryUI(item));
                        }
                        super.updateItem(item, empty);
                    }
                };
            }
        });
        nodeList.get().setCellFactory(new Callback<ListView<NodeProvider>, ListCell<NodeProvider>>() {
            @Override
            public ListCell<NodeProvider> call(ListView<NodeProvider> param) {
                return new ListCell<NodeProvider>() {
                    @Override
                    protected void updateItem(NodeProvider item, boolean empty) {
                        if (item == null) {
                            setGraphic(null);
                        } else {
                            NodeComponentUI comp;
                            setGraphic(comp = new NodeComponentUI(item, module));
                            if (this.getScene() != null && this.getScene().getWindow() != null) {
                                Toolkit.install(comp, new NodePreviewContextMenu((Stage) this.getScene().getWindow(), module, item), 0);
                            }
                        }
                        super.updateItem(item, empty);
                    }
                };
            }
        });
        content.providerProperty().bind(nodeList.get().getSelectionModel().selectedItemProperty());
        nodeList.get().setItems(providers);
        nodeList.get().getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
            if (val != null) {
                if (val instanceof ClassMemberProvider) {
                    DocsUI docsUI = new DocsUI(null, ((ClassMemberProvider) val).getMember());
                    docsUI.setBgColor(30, 30, 30);
                    docsUI.setAllowAdjustingHeight(false);
                    Task.runOnBackground("load-content", docsUI::loadContent);
                    Region scroll = (Region) preview.get().getParent().getParent();
                    docsUI.getViewport().maxHeightProperty().bind(scroll.heightProperty());
                    docsUI.getViewport().maxWidthProperty().bind(scroll.widthProperty());
                    docsUI.getViewport().minHeightProperty().bind(scroll.heightProperty());
                    docsUI.getViewport().minWidthProperty().bind(scroll.widthProperty());
                    preview.get().getChildren().setAll(docsUI.getViewport());
                }
            } else {
                NodeProviderCategory category = categoryList.get().getSelectionModel().getSelectedItem();
                if (category instanceof JavaNodeProviderCategory) {
                    DocsUI docsUI = new DocsUI(null, ((JavaNodeProviderCategory) category).getType());
                    docsUI.setBgColor(30, 30, 30);
                    docsUI.setAllowAdjustingHeight(false);
                    Task.runOnBackground("load-content", docsUI::loadContent);
                    Region scroll = (Region) preview.get().getParent().getParent();
                    docsUI.getViewport().maxHeightProperty().bind(scroll.heightProperty());
                    docsUI.getViewport().maxWidthProperty().bind(scroll.widthProperty());
                    docsUI.getViewport().minHeightProperty().bind(scroll.heightProperty());
                    docsUI.getViewport().minWidthProperty().bind(scroll.widthProperty());
                    preview.get().getChildren().setAll(docsUI.getViewport());
                } else {
                    preview.get().getChildren().clear();
                }
            }
        });
        spinner.get().setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE - 5, 1));
        spinner.get().valueProperty().addListener((obs, old, val) -> {
            NodeProviderCategory category = categoryList.get().getSelectionModel().getSelectedItem();
            if (category instanceof ArrayProviderCategory) {
                ((ArrayProviderCategory) category).setDimension(val.intValue());
                int selectedIndex = nodeList.get().getSelectionModel().getSelectedIndex();
                updateItems(() -> {
                    nodeList.get().getSelectionModel().select(selectedIndex);
                });
            }
        });
        categoryList.get().getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
            if (val instanceof ArrayProviderCategory) {
                if (((ArrayProviderCategory) val).getDimension() != spinner.get().getValue().intValue()) {
                    ((ArrayProviderCategory) val).setDimension(spinner.get().getValue().intValue());
                }
                if (arrayControl.get().getParent() == null) {
                    arrayControlParent.getChildren().add(0, arrayControl.get());
                }
            } else {
                arrayControlParent.getChildren().remove(arrayControl.get());
            }
            updateItems(null);
            if (val instanceof JavaNodeProviderCategory) {
                DocsUI docsUI = new DocsUI(null, ((JavaNodeProviderCategory) val).getType());
                docsUI.setBgColor(30, 30, 30);
                docsUI.setAllowAdjustingHeight(false);
                Task.runOnBackground("load-content", docsUI::loadContent);
                Region scroll = (Region) preview.get().getParent().getParent();
                docsUI.getViewport().maxHeightProperty().bind(scroll.heightProperty());
                docsUI.getViewport().maxWidthProperty().bind(scroll.widthProperty());
                docsUI.getViewport().minHeightProperty().bind(scroll.heightProperty());
                docsUI.getViewport().minWidthProperty().bind(scroll.widthProperty());
                preview.get().getChildren().setAll(docsUI.getViewport());
            }
        });
        overlayCategory.get().visibleProperty().bind(Bindings.isEmpty(categoryList.get().getItems()));
        overlayNode.get().visibleProperty().bind(categoryList.get().getSelectionModel().selectedItemProperty().isNull());
        overlayPreview.get().visibleProperty().bind(nodeList.get().getSelectionModel().selectedItemProperty().isNull());
        if (!lazyLoad) {
            updateSearch();
        }
    }

    public void updateSearch() {
        String search = this.search.get().getText();
        tool.search(search, onlyEvents, map -> {
            Set<NodeProviderCategory> categories = map.keySet();
            Task.runOnForeground("update-category", () -> {
                categoryList.get().getItems().setAll(categories);
                categoryList.get().scrollTo(0);
                tool.markReady();
            });
        });
    }

    public void updateItems(Runnable later) {
        String search = this.search.get().getText();
        if (categoryList.get().getSelectionModel().getSelectedItem() != null) {
            tool2.searchProvider(search, categoryList.get().getSelectionModel().getSelectedItem(), providers -> {
                Task.runOnForeground("update-list", () -> {
                    this.providers.setAll(providers);
                    nodeList.get().scrollTo(0);
                    if (later != null) {
                        later.run();
                    }
                    tool2.markReady();
                });
            });
        } else {
            providers.clear();
        }
    }


}
