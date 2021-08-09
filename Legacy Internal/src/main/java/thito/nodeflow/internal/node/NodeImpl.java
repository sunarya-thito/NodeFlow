package thito.nodeflow.internal.node;

import javafx.collections.*;
import javafx.scene.paint.*;
import javafx.stage.*;
import thito.nodeflow.api.editor.*;
import thito.nodeflow.api.editor.node.Node;
import thito.nodeflow.api.editor.node.NodeParameter;
import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.node.*;
import thito.nodeflow.api.node.state.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.internal.node.provider.*;
import thito.nodeflow.internal.node.state.*;
import thito.nodeflow.internal.ui.*;
import thito.nodeflow.library.binding.*;
import thito.nodejfx.*;
import thito.reflectedbytecode.*;

public class NodeImpl implements Node {

    private NodeModule module;
    private ComponentState state;
    private ObservableList<NodeParameter> parameters = FXCollections.observableArrayList();
    private NodeProvider provider;
    private GenericTypeStorage storage;

    public NodeImpl(NodeModule module, ComponentState state, NodeProvider provider) {
        this.module = module;
        this.state = state;
        ((ComponentStateImpl) state).setModule((StandardNodeModule) module);
        this.provider = provider;
        storage = new GenericTypeStorage();
    }

    public GenericTypeStorage getStorage() {
        return storage;
    }

    @Override
    public void remove() {
        ((AbstractNodeModule) module).nodes().remove(this);
    }

    public NodeModule getModule() {
        return module;
    }

    protected thito.nodejfx.Node createPeer() {
        return new thito.nodejfx.Node();
    }

    private thito.nodejfx.Node node;
    public thito.nodejfx.Node impl_getPeer() {
        if (node == null) {
            node = createPeer();
            if (getState().getProvider() instanceof MethodNodeProvider) {
                Toolkit.install(node.getTitleNode(), () -> new DocsUI((Stage) node.getScene().getWindow(), ((MethodNodeProvider) getState().getProvider()).getMethod()), false);
            } else if (getState().getProvider() instanceof GetFieldNodeProvider) {
                Toolkit.install(node.getTitleNode(), () -> new DocsUI((Stage) node.getScene().getWindow(), ((GetFieldNodeProvider) getState().getProvider()).getField()), false);
            } else if (getState().getProvider() instanceof SetFieldNodeProvider) {
                Toolkit.install(node.getTitleNode(), () ->  new DocsUI((Stage) node.getScene().getWindow(), ((SetFieldNodeProvider) getState().getProvider()).getField()), false);
            } else if (getState().getProvider() instanceof ConstructorNodeProvider) {
                Toolkit.install(node.getTitleNode(), () -> new DocsUI((Stage) node.getScene().getWindow(), ((ConstructorNodeProvider) getState().getProvider()).getConstructor()), false);
            } else if (getState().getProvider() instanceof ImplementationNodeProvider && ((ImplementationNodeProvider) getState().getProvider()).getConstructor() != null) {
                Toolkit.install(node.getTitleNode(), () -> new DocsUI((Stage) node.getScene().getWindow(), ((ImplementationNodeProvider) getState().getProvider()).getConstructor()), false);
            }
            state.getTag().addListener(observable -> {
                if (state.getTag().get() == NodeTag.ERROR) {
                    node.shadowProperty().set(Color.RED);
                } else if (state.getTag().get() == NodeTag.WARNING) {
                    node.shadowProperty().set(Color.GOLD);
                } else if (state.getTag().get() == NodeTag.TODO) {
                    node.shadowProperty().set(Color.AQUA);
                } else {
                    node.shadowProperty().set(NodeContext.SHADOW_NODE);
                }
            });
            if (state.getTag().get() == NodeTag.ERROR) {
                node.shadowProperty().set(Color.RED);
            } else if (state.getTag().get() == NodeTag.WARNING) {
                node.shadowProperty().set(Color.GOLD);
            } else if (state.getTag().get() == NodeTag.TODO) {
                node.shadowProperty().set(Color.AQUA);
            } else {
                node.shadowProperty().set(NodeContext.SHADOW_NODE);
            }
            node.setUserData(this);
            new NodeReachableHandler(node);
            node.titleProperty().set(provider.getName());
            node.subtitleProperty().set(provider.getCategory() instanceof ArrayProviderCategory ? provider.getDescription() : provider.getCategory().getName());
            MappedListBinding.bind(node.getParameters(), parameters, x -> (thito.nodejfx.NodeParameter) x.impl_getPeer())
                    .updatingProperty()
                    .bindBidirectional(MappedListBinding.bind(parameters, node.getParameters(), x -> (NodeParameter) x.getUserData())
                            .updatingProperty());
            node.setLayoutX(state.getX());
            node.setLayoutY(state.getY());
            node.layoutXProperty().addListener((obs, old, val) -> {
                state.setX(val.doubleValue());
            });
            node.layoutYProperty().addListener((obs, old, val) -> {
                state.setY(val.doubleValue());
            });
            for (NodeParameter parameter : parameters) {
                thito.nodejfx.NodeParameter nodeParameter = (thito.nodejfx.NodeParameter) parameter.impl_getPeer();
                nodeParameter.setOnRemovedEvent(event -> {
                    ((StandardNodeModule) module).getSession().getUndoManager().storeAction(I18n.$("action-arguments-remove").stringBinding(),
                            () -> {
                        node.getParameters().add(nodeParameter);
                            }, () -> {
                        nodeParameter.removeParameter();
                            });
//                    node.getParameters().addListener(new UndoableListListener<>(((StandardNodeModule) module).getSession().getUndoManager(),
//                            I18n.$("action-arguments-add"),
//                            I18n.$("action-arguments-remove")
//                    ));
                });
                node.getParameters().add(nodeParameter);
            }
            node.dropPointProperty().addListener((obs, old, val) -> {
                if (old != null && ((StandardNodeModule) module).getSession() != null) {
                    EditorAction.store(((StandardNodeModule) module).getSession(), I18n.$("action-move-node"), () -> {
                        node.setLayoutX(old.getX());
                        node.setLayoutY(old.getY());
                    }, () -> {
                        node.setLayoutX(val.getX());
                        node.setLayoutY(val.getY());
                    });
                }
            });
        }
        return node;
    }

    @Override
    public ComponentState getState() {
        return state;
    }

    @Override
    public ObservableList<NodeParameter> getParameters() {
        return parameters;
    }

    private GMethod compiledMethod;

    public GMethod getCompiledMethod() {
        return compiledMethod;
    }

    public void setCompiledMethod(GMethod method) {
        compiledMethod = method;
    }

}
