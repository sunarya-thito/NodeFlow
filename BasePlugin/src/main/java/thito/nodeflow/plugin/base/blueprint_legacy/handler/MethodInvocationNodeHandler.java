package thito.nodeflow.plugin.base.blueprint_legacy.handler;

import javafx.beans.property.*;
import javafx.scene.image.*;
import thito.nodeflow.engine.node.*;
import thito.nodeflow.language.I18n;
import thito.nodeflow.plugin.base.blueprint_legacy.*;
import thito.nodeflow.plugin.base.blueprint_legacy.state.*;

import java.lang.reflect.*;

public class MethodInvocationNodeHandler extends AbstractMethodCallNodeHandler {
    private Method method;
    private ObjectProperty<Image> icon = new SimpleObjectProperty<>();

    public MethodInvocationNodeHandler(Node node, NodeProvider provider, boolean varargs, Method method) {
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
        return new MethodInvocationNodeState(getProvider());
    }
}
