package thito.nodeflow.internal.ui;

import javafx.beans.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.scene.control.*;
import javafx.util.*;
import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.node.*;
import thito.nodeflow.api.task.*;
import thito.nodeflow.internal.node.*;
import thito.nodeflow.internal.node.provider.*;
import thito.nodeflow.internal.node.search.*;
import thito.nodeflow.internal.ui.editor.*;
import thito.nodeflow.library.ui.layout.*;
import thito.nodejfx.NodeParameter;

public class NodeContextMenuPeer extends UIComponent {

    @Component("list")
    private ObjectProperty<ListView<NodeProvider>> list = new SimpleObjectProperty<>();

    @Component("search")
    private ObjectProperty<TextField> search = new SimpleObjectProperty<>();

    private NodeContextMenu stage;
    private NodeModule module;
    private NodeSearchTool tool = new NodeSearchTool();
    public NodeContextMenuPeer(NodeContextMenu stage, NodeModule module) {
        this.module = module;
        this.stage = stage;
        setLayout(Layout.loadLayout("NodeContextMenuUI"));
    }

    public NodePreviewContextMenu getContextMenu() {
        return contextMenu;
    }

    private NodePreviewContextMenu contextMenu;

    public void updateSearch(String text) {
        tool.search(text, stage.isInput(), stage.getType(), stage.isImplementation(), list -> {
            Task.runOnForeground("update-list", () -> {
                this.list.get().getItems().setAll(list);
                this.list.get().scrollTo(0);
                tool.markReady();
            });
        });
    }

    @Override
    protected void onLayoutReady() {
        list.get().setItems(FXCollections.observableArrayList());
        search.get().textProperty().addListener((obs, old, val) -> {
            if (val != null) {
                updateSearch(val);
            }
        });
        list.get().getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
            if (contextMenu != null) {
                contextMenu.close();
            }
            if (val != null) {
                contextMenu = new NodePreviewContextMenu(stage.getStage(), module, val);
                contextMenu.show(stage.getStage().getX() + stage.getStage().getWidth(), stage.getStage().getY());
                stage.getStage().requestFocus();
            }
        });
        list.get().setCellFactory(new Callback<ListView<NodeProvider>, ListCell<NodeProvider>>() {
            @Override
            public ListCell<NodeProvider> call(ListView<NodeProvider> param) {
                ListCell<NodeProvider> cell = new ListCell<NodeProvider>() {

                    @Override
                    protected void updateItem(NodeProvider item, boolean empty) {
                        if (item == null) {
                            setGraphic(null);
                        } else {
                            setGraphic(new NodeComponentUI(item, null));
                        }
                        super.updateItem(item, empty);
                    }
                };
                cell.setOnMousePressed(event -> {
                    if (event.isPrimaryButtonDown() && event.getClickCount() >= 2 && !cell.isEmpty()) {
                        AbstractNodeProvider provider = (AbstractNodeProvider) cell.getItem();
                        int index = stage.isImplementation() ? provider.getImplementationInput(stage.isInput()) : stage.getType().getParameterIndex(provider, stage.isInput());
                        NodeImpl node = (NodeImpl) provider.createComponent(module);
                        node.impl_getPeer().widthProperty().addListener(new InvalidationListener() {
                            @Override
                            public void invalidated(Observable observable) {
                                if (!stage.isInput()) {
                                    node.impl_getPeer().setLayoutX(stage.getX() - node.impl_getPeer().getWidth());
                                }
                                observable.removeListener(this);
                            }
                        });
                        if (index >= 0) {
                            NodeParameter parameter = (NodeParameter) node.getParameters().get(index).impl_getPeer();
                            parameter.layoutYProperty().addListener(new InvalidationListener() {
                                @Override
                                public void invalidated(Observable observable) {
                                    node.impl_getPeer().setLayoutY(stage.getY() - parameter.getLayoutY() - parameter.getHeight() / 2d);
                                    observable.removeListener(this);
                                }
                            });
                        }
                        ((StandardNodeModule) module).nodes().add(node);
                        node.impl_getPeer().setLayoutX(stage.getX());
                        node.impl_getPeer().setLayoutY(stage.getY());
                        Task.runOnForeground("connect", () -> {
                            /*
                            REMOVAL OF THIS BEHAVIOUR
                            Due to variety of Nodes, this behaviour should not be used as the prediction can
                            cause several issues
                             */
//                            if (sourceExecutionIndex >= 0 && executionIndex >= 0 && !(stage.getParameter() instanceof ExecutionComponentParameter.ExecutionParameter)) {
//                                if (stage.isInput()) {
//                                    ((NodeModuleImpl) module).links().add(new LinkImpl(module, stage.getParameter().getComponent().getParameters()[sourceExecutionIndex], component.getParameters()[executionIndex]));
//                                } else {
//                                    ((NodeModuleImpl) module).links().add(new LinkImpl(module, component.getParameters()[executionIndex], stage.getParameter().getComponent().getParameters()[sourceExecutionIndex]));
//                                }
//                            }
                            if (index >= 0) {
                                if (stage.isInput()) {
                                    ((StandardNodeModule) module).links().add(new LinkImpl(module, stage.getParameter(), node.getParameters().get(index)));
                                } else {
                                    ((StandardNodeModule) module).links().add(new LinkImpl(module, node.getParameters().get(index), stage.getParameter()));
                                }
                            }
                        });
                        stage.close();
                        contextMenu.close();
                    }
                });
                return cell;
            }
        });
        updateSearch("");
    }

}
