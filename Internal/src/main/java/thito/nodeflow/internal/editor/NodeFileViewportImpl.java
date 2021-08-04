package thito.nodeflow.internal.editor;

import javafx.application.*;
import javafx.beans.Observable;
import javafx.beans.*;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.paint.*;
import thito.nodeflow.api.editor.node.Node;
import thito.nodeflow.api.editor.node.NodeGroup;
import thito.nodeflow.api.editor.node.NodeParameter;
import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.node.state.*;
import thito.nodeflow.api.project.*;
import thito.nodeflow.api.task.*;
import thito.nodeflow.api.ui.dialog.*;
import thito.nodeflow.api.ui.menu.MenuItem;
import thito.nodeflow.api.ui.menu.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.internal.clipboard.*;
import thito.nodeflow.internal.node.*;
import thito.nodeflow.internal.node.provider.*;
import thito.nodeflow.internal.node.provider.minecraft.*;
import thito.nodeflow.internal.node.search.*;
import thito.nodeflow.internal.node.state.*;
import thito.nodeflow.internal.ui.*;
import thito.nodeflow.internal.ui.dialog.*;
import thito.nodeflow.library.binding.*;
import thito.nodejfx.*;
import thito.nodejfx.event.*;
import thito.nodejfx.parameter.*;
import thito.nodejfx.parameter.type.*;

import java.util.*;

public class NodeFileViewportImpl {

    private NodeEditor editor;
    private NodeFileSession session;
    private NodeContainer chestContainer;
    private ContextMenu contextMenu;
    private ObservableList<MenuItem> menuItems = FXCollections.observableArrayList();
    private boolean updatingSelected = false;
    private Project project;
    public NodeFileViewportImpl(Project project, NodeFileSession session) {
        this.project = project;
        this.session = session;
        editor = new NodeEditor();
        chestContainer = new NodeContainer(editor.getCanvas());
        editor.getCanvas().getChildren().add(1, chestContainer);
        editor.getCanvas().setUserData(this);
        editor.getCanvas().setHandleNodeRemoval(false);
        editor.getCanvas().getSelectedNodes().addListener((SetChangeListener<NodeCanvasElement>) change -> {
            if (updatingSelected) return;
            updatingSelected = true;
            if (change.wasAdded()) {
                NodeCanvasElement element = change.getElementAdded();
                if (element instanceof javafx.scene.Node) {
                    Object userData = ((javafx.scene.Node) element).getUserData();
                    if (userData instanceof ModuleMember) {
                        session.getSelected().add((ModuleMember) userData);
                    }
                }
            }
            if (change.wasRemoved()) {
                NodeCanvasElement element = change.getElementRemoved();
                if (element instanceof javafx.scene.Node) {
                    Object userData = ((javafx.scene.Node) element).getUserData();
                    if (userData instanceof ModuleMember) {
                        session.getSelected().remove((ModuleMember) userData);
                    }
                }
            }
            updatingSelected = false;
        });
        session.getSelected().addListener((SetChangeListener<ModuleMember>) change -> {
            if (change.wasAdded()) {
                ModuleMember member = change.getElementAdded();
                if (member instanceof NodeImpl) {
                    editor.getCanvas().getSelectedNodes().add(((NodeImpl) member).impl_getPeer());
                }
                if (member instanceof NodeGroupImpl) {
                    editor.getCanvas().getSelectedNodes().add(((NodeGroupImpl) member).impl_getPeer());
                }
            }
            if (change.wasRemoved()) {
                ModuleMember member = change.getElementRemoved();
                if (member instanceof NodeImpl) {
                    editor.getCanvas().getSelectedNodes().remove(((NodeImpl) member).impl_getPeer());
                }
                if (member instanceof NodeGroupImpl) {
                    editor.getCanvas().getSelectedNodes().remove(((NodeGroupImpl) member).impl_getPeer());
                }
            }
        });
        load();
        editor.setEffect(new InnerShadow(50, Color.color(0, 0, 0, 0.8)));
        contextMenu = new ContextMenu();
        MappedListBinding.bind(contextMenu.getItems(), menuItems, MenuItem::impl_createPeer);
        prepareDefaultContextMenu();
        editor.setOnContextMenuRequested(event -> {
            event.consume();
            contextMenu.show(Toolkit.getWindow(editor).impl_getPeer(), event.getScreenX(), event.getScreenY());
        });
        editor.addEventHandler(NodeLinkEvent.NODE_LINK_CANCEL_EVENT, event -> {
            NodeLinking linking = event.getLinking();
            DoubleProperty x = linking.getEndX();
            DoubleProperty y = linking.getEndY();
            thito.nodejfx.NodeParameter parameter;
            if (linking.isInput()) {
                parameter = event.getNodeInput();
            } else {
                parameter = event.getNodeOutput();
            }
            Point2D point = editor.getCanvas().getLinkContainer().localToScreen(x.get(), y.get());
            linking.setHold(true);
            NodeParameter nodeParameter = (NodeParameter) parameter.getUserData();
            Object type = nodeParameter.impl_getType();
            if (type instanceof JavaParameterType) {
                Class<?> clazz = ((JavaParameterType<?>) type).getType();
                NodeContextMenu menu = new NodeContextMenu((WindowImpl) Toolkit.getWindow(editor), session.getModule(), x.get(), y.get(), new ExpectingType(clazz), !linking.isInput(), nodeParameter, false);
                menu.getStage().setOnHidden(event2 -> {
                    linking.remove();
                });
                menu.show(point.getX(), point.getY());
            } else if (type instanceof MethodOverrideNodeParameter.MethodOverrideType) {
                Class<?> clazz = ((MethodOverrideNodeParameter.MethodOverrideType) type).getType();
                NodeContextMenu menu = new NodeContextMenu((WindowImpl) Toolkit.getWindow(editor), session.getModule(), x.get(), y.get(), new ExpectingType(clazz), !linking.isInput(), nodeParameter, true);
                menu.getStage().setOnHidden(event2 -> {
                    linking.remove();
                });
                menu.show(point.getX(), point.getY());
            } else if (type instanceof CompoundType) {
                NodeContextMenu menu = new NodeContextMenu((WindowImpl) Toolkit.getWindow(editor), session.getModule(), x.get(), y.get(), new ExpectingType(((CompoundType) type).getClasses()), !linking.isInput(), nodeParameter, false);
                menu.getStage().setOnHidden(event2 -> {
                    linking.remove();
                });
                menu.show(point.getX(), point.getY());
            } else {
                ObjectProperty<Node> component = new SimpleObjectProperty<>();
                OpenedDialog dialog = Dialogs.addNode(Toolkit.getWindow(editor), project, session, point.getX(), point.getY(), true, false, component);
                component.addListener((obs, old, val) -> {
                    NodeImpl node = (NodeImpl) val;
                    Point2D px = editor.getCanvas().getNodeContainer().screenToLocal(point);
                    AbstractNodeProvider provider = (AbstractNodeProvider) val.getState().getProvider();
                    int index;
                    if (linking.isInput()) {
                        index = provider.getParameterIndex(true, null);
                        if (index >= 0) {
                            editor.getCanvas().connect(((NodeImpl) val).impl_getPeer().getParameters().get(index), parameter);
                        }
                    } else {
                        index = provider.getParameterIndex(false, null);
                        if (index >= 0) {
                            editor.getCanvas().connect(parameter, ((NodeImpl) val).impl_getPeer().getParameters().get(index));
                        }
                    }
                    node.impl_getPeer().widthProperty().addListener(new InvalidationListener() {
                        @Override
                        public void invalidated(Observable observable) {
                            if (linking.isInput()) {
                                node.impl_getPeer().setLayoutX(px.getX() - node.impl_getPeer().getWidth());
                            } else {
                                node.impl_getPeer().setLayoutX(px.getX());
                            }
                            observable.removeListener(this);
                        }
                    });
                    if (index >= 0) {
                        thito.nodejfx.NodeParameter nodePx = (thito.nodejfx.NodeParameter) node.getParameters().get(index).impl_getPeer();
                        nodePx.layoutYProperty().addListener(new InvalidationListener() {
                            @Override
                            public void invalidated(Observable observable) {
                                node.impl_getPeer().setLayoutY(px.getY() - nodePx.getLayoutY() - nodePx.getHeight() / 2d);
                                observable.removeListener(this);
                            }
                        });
                    }
                });
                ((OpenedDialogImpl) dialog).getStage().setOnHidden(event2 -> {
                    linking.remove();
                });
                return;
            }

        });

        session.alwaysAnimateProperty().addListener((obs, old, val) -> {
            for (Link link : session.getModule().links()) {
                NodeLinked linked = ((LinkImpl) link).impl_getPeer();
                if (linked != null) {
                    linked.getStyle().setActive(val);
                }
            }
        });

        // should we tho?
//        editor.getViewport().dropPointProperty().addListener((obs, old, val) -> {
//            if (old != null && session.isLoaded()) {
//                EditorAction.store(I18n.$("action-pan-canvas"), ((NodeModuleImpl) session.getModule())::valid, () -> {
//                    editor.getViewport().paneX().set(old.getX());
//                    editor.getViewport().paneY().set(old.getY());
//                }, () -> {
//                    editor.getViewport().paneX().set(val.getX());
//                    editor.getViewport().paneY().set(val.getY());
//                });
//            }
//        });

        session.styleProperty().addListener((obs, old, val) -> {
            switch (val) {
                case PIPE:
                    editor.getCanvas().nodeLinkStyleProperty().set(NodeLinkStyle.PIPE_STYLE);
                    break;
                case LINEAR:
                    editor.getCanvas().nodeLinkStyleProperty().set(NodeLinkStyle.LINE_STYLE);
                    break;
                case CURVE:
                    editor.getCanvas().nodeLinkStyleProperty().set(NodeLinkStyle.BEZIER_STYLE);
                    break;
            }
        });

    }

    private void prepareDefaultContextMenu() {
        menuItems.add(MenuItem.create(I18n.$("editor-menu-new"), () -> {
            Dialogs.addNode(Toolkit.getWindow(editor), project, session, contextMenu.getX(), contextMenu.getY(), false, true);
        }, MenuItemType.BUTTON_TYPE));
        menuItems.add(MenuItem.create(I18n.$("editor-menu-new-group"), () -> {
            Point2D point = editor.getCanvas().screenToLocal(contextMenu.getX(), contextMenu.getY());
            GroupStateImpl state = new GroupStateImpl(session.getModule());
            state.setMinX(point.getX());
            state.setMaxX(point.getX() + 100);
            state.setMinY(point.getY());
            state.setMaxY(point.getY() + 100);
            state.setName(I18n.$("group-untitled").getString());
            session.getModule().groups().add(new NodeGroupImpl(session.getModule(), state));
        }, MenuItemType.BUTTON_TYPE));
        menuItems.add(MenuItem.createSeparator());
        MenuItem cut = MenuItem.create(I18n.$("tool-cut"), () -> {
            Set<ModuleMember> selected = session.getSelected();
            ModuleMemberClipboard.putIntoClipboard(selected);
            for (ModuleMember member : selected) {
                member.remove();
            }
            selected.clear();
        }, MenuItemType.BUTTON_TYPE);
        cut.impl_disableProperty().bind(Bindings.isEmpty(session.getSelected()));
        menuItems.add(cut);
        MenuItem copy = MenuItem.create(I18n.$("tool-copy"), () -> {
            ModuleMemberClipboard.putIntoClipboard(session.getSelected());
        }, MenuItemType.BUTTON_TYPE);
        copy.impl_disableProperty().bind(Bindings.isEmpty(session.getSelected()));
        menuItems.add(copy);
        MenuItem paste = MenuItem.create(I18n.$("tool-paste"), () -> {
            Point2D point = editor.getCanvas().screenToLocal(contextMenu.getX(), contextMenu.getY());
            Set<ModuleMember> selected = session.getSelected();
            ModuleMemberClipboard.paste(point, selected, session.getModule());
        }, MenuItemType.BUTTON_TYPE);
        paste.impl_disableProperty().bind(ModuleMemberClipboard.HAS_DATA.not());
        menuItems.add(paste);
    }

    public List<MenuItem> getItems() {
        return menuItems;
    }

    // no need encapsulation?
    public boolean updatingNodes, updatingGroups, updatingLinks;
    private void load() {
        NodeViewport vp = editor.getViewport();
        try {
            StandardNodeModule module = session.getModule();
            vp.paneX().set(module.getEditorState().getOffsetX());
            vp.paneY().set(module.getEditorState().getOffsetY());
            vp.scaleX().set(Math.max(0.5, Math.min(3, module.getEditorState().getZoom())));
            vp.scaleY().set(Math.max(0.5, Math.min(3, module.getEditorState().getZoom())));
            vp.paneX().addListener((obs, old, val) -> {
                module.getEditorState().setOffsetX(val.doubleValue());
                session.save();
            });
            vp.paneY().addListener((obs, old, val) -> {
                module.getEditorState().setOffsetY(val.doubleValue());
                session.save();
            });
            vp.scaleX().addListener((obs, old, val) -> {
                module.getEditorState().setZoom(val.doubleValue());
                session.save();
            });
            Task.runOnForeground("load-components", () -> {
                InvalidationListener listener = session::saveObs;
                for (Node node : module.nodes()) {
                    thito.nodejfx.Node nx;
                    if (node.getState().getProvider() instanceof ChestMenuProvider) {
                        chestContainer.getNodes().add(nx = ((NodeImpl) node).impl_getPeer());
                    } else {
                        vp.getCanvas().getNodes().add(nx = ((NodeImpl) node).impl_getPeer());
                    }
                    nx.layoutXProperty().addListener(listener);
                    nx.layoutYProperty().addListener(listener);
                    for (thito.nodejfx.NodeParameter param : nx.getParameters()) {
                        if (param instanceof UserInputParameter) {
                            ((UserInputParameter<?>) param).valueProperty().addListener(listener);
                        }
                    }
                }
                for (NodeGroup nodeGroup : module.getGroups()) {
                    thito.nodejfx.NodeGroup gx;
                    vp.getCanvas().getGroups().add(gx = ((NodeGroupImpl) nodeGroup).impl_getPeer());
                    gx.getGroupName().addListener(listener);
                    gx.getTopPos().addListener(listener);
                    gx.getBottomPos().addListener(listener);
                    gx.getLeftPos().addListener(listener);
                    gx.getRightPos().addListener(listener);
                }
                for (Link link : new ArrayList<>(module.links())) {
                    NodeLinked linked = vp.getCanvas().connect(((NodeParameterImpl) link.getSource()).getParameter(), ((NodeParameterImpl) link.getTarget()).getParameter());
                    if (linked != null) {
                        ((LinkImpl) link).impl_setPeer(linked);
                        linked.getStyle().setActive(session.alwaysAnimateProperty().get());
                    } else {
                        module.links().remove(link);
                    }
                }
                module.nodes().addListener((SetChangeListener<Node>) change -> {
                    if (updatingNodes) return;
                    updatingNodes = true;
                    if (change.wasAdded()) {
                        vp.getCanvas().getNodes().add(((NodeImpl) change.getElementAdded()).impl_getPeer());
                    }
                    if (change.wasRemoved()) {
                        vp.getCanvas().getNodes().remove(((NodeImpl) change.getElementRemoved()).impl_getPeer());
                    }
                    updatingNodes = false;
                });
                module.groups().addListener((SetChangeListener<NodeGroup>) change -> {
                    if (updatingGroups) return;
                    updatingGroups = true;
                    if (change.wasAdded()) {
                        thito.nodejfx.NodeGroup nodeGroup;
                        vp.getCanvas().getGroups().add(nodeGroup = ((NodeGroupImpl) change.getElementAdded()).impl_getPeer());
                        nodeGroup.getGroupName().addListener(listener);
                        nodeGroup.getTopPos().addListener(listener);
                        nodeGroup.getBottomPos().addListener(listener);
                        nodeGroup.getLeftPos().addListener(listener);
                        nodeGroup.getRightPos().addListener(listener);
                    }
                    if (change.wasRemoved()) {
                        thito.nodejfx.NodeGroup nodeGroup;
                        vp.getCanvas().getGroups().remove(nodeGroup = ((NodeGroupImpl) change.getElementRemoved()).impl_getPeer());
                        nodeGroup.getGroupName().removeListener(listener);
                        nodeGroup.getTopPos().removeListener(listener);
                        nodeGroup.getBottomPos().removeListener(listener);
                        nodeGroup.getLeftPos().removeListener(listener);
                        nodeGroup.getRightPos().removeListener(listener);
                    }
                    updatingGroups = false;
                });
                module.links().addListener((SetChangeListener<Link>) change -> {
                    if (updatingLinks) return;
                    updatingLinks = true;
                    Link link;
                    if (change.wasAdded()) {
                        link = change.getElementAdded();
                        NodeReachableHandler handler = NodeReachableHandler.get(((NodeImpl) link.getSource().getNode()).impl_getPeer());
                        if (!handler.hasTheSameRoot(((NodeImpl) link.getTarget().getNode()).impl_getPeer())) {
                            Link finalLink1 = link;
                            Platform.runLater(() -> {
                                module.links().remove(finalLink1);
                            });
                        } else {
                            NodeLinked linked = vp.getCanvas().forceConnect(((NodeParameterImpl) link.getSource()).getParameter(), ((NodeParameterImpl) link.getTarget()).getParameter());
                            ((LinkImpl) link).impl_setPeer(linked);
                            linked.getStyle().setActive(session.alwaysAnimateProperty().get());
                        }
                    }

                    if (change.wasRemoved()) {
                        link = change.getElementRemoved();
                        vp.getCanvas().disconnect(((NodeParameterImpl) link.getSource()).getParameter(), ((NodeParameterImpl) link.getTarget()).getParameter());
                        ((LinkImpl) link).impl_setPeer(null);
                    }
                    updatingLinks = false;
                });
                editor.getCanvas().getGroups().addListener((ListChangeListener<thito.nodejfx.NodeGroup>) c -> {
                    if (updatingGroups) return;
                    updatingGroups = true;
                    while (c.next()) {
                        for (thito.nodejfx.NodeGroup nodeGroup : c.getRemoved()) {
                            module.groups().remove(nodeGroup.getUserData());
                            nodeGroup.getGroupName().removeListener(listener);
                            nodeGroup.getTopPos().removeListener(listener);
                            nodeGroup.getBottomPos().removeListener(listener);
                            nodeGroup.getLeftPos().removeListener(listener);
                            nodeGroup.getRightPos().removeListener(listener);
                        }
                        for (thito.nodejfx.NodeGroup nodeGroup : c.getAddedSubList()) {
                            NodeGroupImpl impl = (NodeGroupImpl) nodeGroup.getUserData();
                            if (impl == null) {
                                GroupState state = new GroupStateImpl(session.getModule());
                                state.setMinX(nodeGroup.getLeftPos().get());
                                state.setMinY(nodeGroup.getTopPos().get());
                                state.setMaxX(nodeGroup.getRightPos().get());
                                state.setMaxY(nodeGroup.getBottomPos().get());
                                impl = new NodeGroupImpl(session.getModule(), state);
                                nodeGroup.setUserData(impl);
                                nodeGroup.getGroupName().addListener(listener);
                                nodeGroup.getTopPos().addListener(listener);
                                nodeGroup.getBottomPos().addListener(listener);
                                nodeGroup.getLeftPos().addListener(listener);
                                nodeGroup.getRightPos().addListener(listener);
                            }
                            module.groups().add(impl);
                        }
                    }
                    updatingGroups = false;
                });
                editor.getCanvas().getNodes().addListener((ListChangeListener<thito.nodejfx.Node>) c -> {
                    if (updatingNodes) return;
                    updatingNodes = true;
                    while (c.next()) {
                        for (thito.nodejfx.Node node : c.getRemoved()) {
                            module.nodes().remove(node.getUserData());
                            node.layoutXProperty().removeListener(listener);
                            node.layoutYProperty().removeListener(listener);
                            for (thito.nodejfx.NodeParameter param : node.getParameters()) {
                                if (param instanceof UserInputParameter) {
                                    ((UserInputParameter<?>) param).valueProperty().removeListener(listener);
                                }
                            }
                        }
                        for (thito.nodejfx.Node node : c.getAddedSubList()) {
                            node.layoutXProperty().addListener(listener);
                            node.layoutYProperty().addListener(listener);
                            for (thito.nodejfx.NodeParameter param : node.getParameters()) {
                                if (param instanceof UserInputParameter) {
                                    ((UserInputParameter<?>) param).valueProperty().addListener(listener);
                                }
                            }
                        }
                        // Node added externally? HELL NO!
//                        for (Node node : c.getAddedSubList()) {
//                        }
                    }
                    updatingNodes = false;
                });
            });
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public NodeEditor getEditor() {
        return editor;
    }

}
