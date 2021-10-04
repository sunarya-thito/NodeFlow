package thito.nodeflow.plugin.base.blueprint.handler.parameter;

import javafx.beans.property.*;
import javafx.scene.paint.*;
import thito.nodeflow.engine.node.*;
import thito.nodeflow.engine.node.skin.*;
import thito.nodeflow.engine.node.state.*;
import thito.nodeflow.internal.language.*;
import thito.nodeflow.plugin.base.blueprint.*;
import thito.nodeflow.plugin.base.blueprint.state.parameter.*;

import java.lang.reflect.*;

public class OutputParameterHandler extends JavaParameterHandler {
    private GenericStorage genericStorage;
    private Type type;
    private NodePort outputPort;
    public OutputParameterHandler(GenericStorage genericStorage, Type type, NodeParameter parameter) {
        super(parameter);
        this.type = type;
        this.genericStorage = genericStorage;
        this.outputPort = new NodePort(true, Color.BLACK, PortShape.CIRCLE);
        this.outputPort.colorProperty().bind(BlueprintRegistry.getTypeColor(genericStorage, type));
    }

    @Override
    public GenericStorage getGenericStorage() {
        return genericStorage;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public StringProperty displayNameProperty() {
        return I18n.$("plugin.blueprint.return-param");
    }

    @Override
    public NodeParameterSkin createSkin() {
        return new NodeParameterSkin(getParameter());
    }

    @Override
    public NodePort getInputPort() {
        return null;
    }

    @Override
    public NodePort getOutputPort() {
        return outputPort;
    }

    @Override
    public HandlerState saveState() {
        return new OutputParameterHandlerState();
    }
}
