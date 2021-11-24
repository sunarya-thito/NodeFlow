package thito.nodeflow.plugin.base.blueprint_legacy.handler;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import thito.nodeflow.engine.node.Node;
import thito.nodeflow.language.I18n;
import thito.nodeflow.plugin.base.blueprint_legacy.NodeProvider;
import thito.nodeflow.plugin.base.blueprint_legacy.state.BlueprintNodeState;
import thito.nodeflow.plugin.base.blueprint_legacy.state.EventMethodInvocationNodeState;

import java.lang.reflect.Executable;
import java.lang.reflect.Method;

public class EventMethodInvocationNodeHandler extends AbstractMethodCallNodeHandler {
    private Method method;
    private ObjectProperty<Image> icon = new SimpleObjectProperty<>();

    public EventMethodInvocationNodeHandler(Node node, NodeProvider provider, boolean varargs, Method method) {
        super(node, provider, varargs);
        this.method = method;
    }

    @Override
    protected Executable getExecutable() {
        return method;
    }

    @Override
    public StringProperty displayNameProperty() {
        return I18n.$("plugin.blueprint.method-node").format(method.getName());
    }

    @Override
    public ObjectProperty<Image> iconProperty() {
        return icon;
    }

    @Override
    public BlueprintNodeState saveState() {
        return new EventMethodInvocationNodeState(getProvider());
    }
}
