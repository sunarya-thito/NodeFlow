package thito.nodeflow.plugin.base.blueprint_legacy.handler;

import javafx.beans.property.*;
import javafx.scene.image.*;
import thito.nodeflow.engine.node.*;
import thito.nodeflow.language.I18n;
import thito.nodeflow.plugin.base.blueprint_legacy.*;
import thito.nodeflow.plugin.base.blueprint_legacy.state.*;

import java.lang.reflect.*;

public class ConstructorNodeHandler extends AbstractMethodCallNodeHandler {
    private final Constructor<?> constructor;
    private final ObjectProperty<Image> icon = new SimpleObjectProperty<>();

    public ConstructorNodeHandler(Node node, NodeProvider provider, boolean varargs, Constructor<?> constructor) {
        super(node, provider, varargs);
        this.constructor = constructor;
    }

    @Override
    protected Executable getExecutable() {
        return constructor;
    }

    @Override
    public BlueprintNodeState saveState() {
        return new ConstructorNodeState(getProvider());
    }

    @Override
    public StringProperty displayNameProperty() {
        return I18n.$("plugin.blueprint.constructor-node").format(constructor.getDeclaringClass().getSimpleName(),
                constructor.getDeclaringClass().getName(), constructor.getDeclaringClass().getCanonicalName());
    }

    @Override
    public ObjectProperty<Image> iconProperty() {
        return icon;
    }
}
