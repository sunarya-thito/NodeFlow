package thito.nodeflow.internal.ui.editor;

import com.jfoenix.controls.*;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.util.*;
import thito.nodeflow.api.project.*;
import thito.nodeflow.api.resource.*;
import thito.nodeflow.api.task.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.internal.editor.menu.*;
import thito.nodeflow.internal.project.*;
import thito.nodeflow.internal.ui.*;
import thito.nodeflow.library.ui.*;
import thito.nodeflow.library.ui.layout.*;

public class EditorUI extends UIComponent {
    @Component("explorer")
    private final ObjectProperty<FileExplorer> explorer = new SimpleObjectProperty<>();

    @Component("center")
    private final ObjectProperty<JFXTabPane> center = new SimpleObjectProperty<>();

    @Component("left")
    private final ObjectProperty<JFXTabPane> left = new SimpleObjectProperty<>();

    @Component("toolbar")
    private final ObjectProperty<HBox> toolbar = new SimpleObjectProperty<>();

    @Component("property_list")
    private final ObjectProperty<VBox> propertyList = new SimpleObjectProperty<>();

    private EditorWindowImpl window;
    public EditorUI(EditorWindowImpl editorWindow) {
        window = editorWindow;
        setLayout(Layout.loadLayout("EditorUI"));
    }

    public VBox getPropertyList() {
        return propertyList.get();
    }

//    private ToolSeparatorImpl separator = new ToolSeparatorImpl();

    public void addTab(ProjectTab tab) {
        center.get().getTabs().add((ProjectTabImpl)tab);
//        Tab tx;
//        center.get().getTabs().add(tx = new Tab("", tab.impl_getPeer()));
//        tx.textProperty().bind(tab.impl_titleProperty());
//        ((ProjectTabImpl) tab).setTab(tx);
//        ((ProjectTabImpl) tab).sessionProperty().addListener((observable, old, val) -> Task.runOnForeground("Add Toolbar", () -> {
//            if (val != null) {
//                Toolbar toolbar = val.getToolbar();
//                if (toolbar != null) {
//                    HBox tool = EditorUI.this.toolbar.get();
//                    center.get().getSelectionModel().selectedItemProperty().addListener(new WeakReferencedChangeListener<Tab>(new WeakReference<>(tx)) {
//                        @Override
//                        public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
//                            if (newValue == tx) {
//                                tool.getChildren().add(separator.impl_getPeer());
//                                tool.getChildren().add(toolbar.impl_getPeer());
//                                Bindings.bindContentBidirectional(window.getProperties(), tab.getTabProperties());
//                            } else {
//                                tool.getChildren().remove(separator.impl_getPeer());
//                                tool.getChildren().remove(toolbar.impl_getPeer());
//                                window.getProperties().clear();
//                                Bindings.unbindContentBidirectional(window.getProperties(), tab.getTabProperties());
//                            }
//                        }
//                    });
//                    if (center.get().getTabs().size() == 1) {
//                        if (old != null) {
//                            Toolbar otb = old.getToolbar();
//                            if (otb != null) {
//                                tool.getChildren().remove(otb.impl_getPeer());
//                            }
//                        }
//                        if (!tool.getChildren().contains(separator.impl_getPeer())) {
//                            tool.getChildren().add(separator.impl_getPeer());
//                        }
//                        tool.getChildren().add(toolbar.impl_getPeer());
//                    }
//                    Bindings.unbindContentBidirectional(window.getProperties(), tab.getTabProperties()); // just making sure
//                    Bindings.bindContentBidirectional(window.getProperties(), tab.getTabProperties());
//                }
//            }
//        }));
        center.get().getSelectionModel().select((ProjectTabImpl) tab);
    }

    public FileExplorer getExplorer() {
        return explorer.get();
    }

    public EditorWindowImpl getWindow() {
        return window;
    }

    public void removeTab(ProjectTab tab) {
        center.get().getTabs().removeIf(x -> x.getContent() == tab.impl_getPeer());
    }

    private OverlayUI overlay;
    public void setOverlay(OverlayUI overlay) {
        Task.runOnForeground("set-overlay", () -> {
            getChildren().remove(this.overlay);
            getChildren().remove(overlay);
            this.overlay = overlay;
            if (overlay != null) {
                getChildren().add(overlay);
            }
        });
    }

    @Override
    protected void onLayoutReady() {
        explorer.get().setWindow(this);

        DefaultToolbar defTool = new DefaultToolbar(this);
        toolbar.get().getChildren().add(defTool.impl_getPeer());

        Resource unknown = window.getProject().getProperties().getDirectory().getChild("src");
        if (unknown instanceof PhysicalResource) {
            ((PhysicalResource) unknown).delete();
            unknown = window.getProject().getProperties().getDirectory().getChild("src");
        }
        if (unknown instanceof UnknownResource) {
            unknown = ((UnknownResource) unknown).createDirectory();
        }
        ResourceDirectory directory = (ResourceDirectory) unknown;
        explorer.get().setRoot(directory);
        left.get().setDisableAnimation(true);
        center.get().setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        Callback<TreeView<Resource>, TreeCell<Resource>> factory = explorer.get().getResourceTree().getCellFactory();
        center.get().getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
            HBox box = toolbar.get();
            if (old != null) {
                box.getChildren().remove(((ProjectTabImpl) old).getToolbarPeer());
                Bindings.unbindContent(window.getProperties(), ((ProjectTabImpl) old).getTabProperties());
                window.getProperties().clear();
            }
            if (val != null) {
                box.getChildren().add(((ProjectTabImpl) val).getToolbarPeer());
                Bindings.bindContent(window.getProperties(), ((ProjectTabImpl) val).getTabProperties());
            }
        });
        explorer.get().getListeners().add(new FileExplorer.FileListener() {
            @Override
            public void onFileChange(ResourceWatcherEvent.Type type, Resource resource) {
                switch (type) {
                    case MODIFY:
                    case CREATE:
                        if (resource instanceof ResourceFile && ((ResourceFile) resource).getExtension().equals("cfg")) {
                            ((ProjectImpl) getWindow().getProject()).refreshVariables();
                        }
                        if (resource instanceof ResourceFile && ((ResourceFile) resource).getExtension().equals("rec")) {
                            ((ProjectImpl) getWindow().getProject()).refreshRecords();
                        }
                        if (resource instanceof ResourceFile && (((ResourceFile) resource).getExtension().equals("yml") || ((ResourceFile) resource).getExtension().equals("yaml"))) {
                            ((ProjectImpl) getWindow().getProject()).refreshYaml();
                        }
                        break;
                    case DELETE:
                        if (resource instanceof ResourceFile && ((ResourceFile) resource).getExtension().equals("cfg")) {
                            ((ProjectImpl) getWindow().getProject()).refreshVariables();
                        }
                        if (resource instanceof ResourceFile && ((ResourceFile) resource).getExtension().equals("rec")) {
                            ((ProjectImpl) getWindow().getProject()).refreshRecords();
                        }
                        if (resource instanceof ResourceFile && (((ResourceFile) resource).getExtension().equals("yml") || ((ResourceFile) resource).getExtension().equals("yaml"))) {
                            ((ProjectImpl) getWindow().getProject()).refreshYaml();
                        }
                        getWindow().getTabs().removeIf(tabs -> {
                            if (tabs.getFile().getPath().equals(resource.getPath())) {
                                tabs.closeFile();
                                center.get().getTabs().removeIf(tab -> tab.getContent() == tabs.impl_getPeer());
                                return true;
                            }
                            return false;
                        });
                        break;
                }
            }

            @Override
            public void onFileMoved(Resource from, Resource to) {
            }
        });
        explorer.get().getResourceTree().setCellFactory(param -> {
            TreeCell<Resource> resourceTreeCell = factory.call(param);
            resourceTreeCell.setOnMouseClicked(event -> {
                event.consume();
                if (event.getClickCount() >= 2 && event.getButton() == MouseButton.PRIMARY) {
                    explorer.get().getResourceTree().getSelectionModel().getSelectedItems().forEach(selected -> {
                        if (selected.getValue() instanceof ResourceFile) {
                            Toolkit.info("Opening file "+selected.getValue().getPath());
                            window.openFile((ResourceFile) selected.getValue());
                        }
                    });
                }
            });
            return resourceTreeCell;
        });
    }
}
