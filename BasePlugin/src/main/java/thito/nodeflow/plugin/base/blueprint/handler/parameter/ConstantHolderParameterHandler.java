package thito.nodeflow.plugin.base.blueprint.handler.parameter;

import javafx.beans.property.*;
import thito.nodeflow.engine.node.*;
import thito.nodeflow.engine.node.skin.*;
import thito.nodeflow.plugin.base.blueprint.state.parameter.*;

public abstract class ConstantHolderParameterHandler extends JavaParameterHandler {
    private ObjectProperty<Object> value = new SimpleObjectProperty<>();

    public ConstantHolderParameterHandler(NodeParameter parameter) {
        super(parameter);
    }

    @Override
    public NodeParameterSkin createSkin() {
        return null;
    }

    public Object getValue() {
        return value.get();
    }

    public ObjectProperty<Object> valueProperty() {
        return value;
    }

    public void setValue(Object value) {
        this.value.set(value);
    }

    @Override
    public final ConstantHolderParameterHandlerState saveState() {
        ConstantHolderParameterHandlerState state = subSaveState();
        state.value = getValue();
        return state;
    }

    protected abstract ConstantHolderParameterHandlerState subSaveState();
}
