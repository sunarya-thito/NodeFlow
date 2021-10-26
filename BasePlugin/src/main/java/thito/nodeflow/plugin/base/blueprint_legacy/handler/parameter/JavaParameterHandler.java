package thito.nodeflow.plugin.base.blueprint_legacy.handler.parameter;

import org.apache.commons.lang3.reflect.*;
import thito.nodeflow.engine.node.*;
import thito.nodeflow.engine.node.handler.*;
import thito.nodeflow.plugin.base.blueprint_legacy.*;

import java.lang.reflect.*;

public abstract class JavaParameterHandler implements NodeParameterHandler {

    private NodeParameter parameter;

    public JavaParameterHandler(NodeParameter parameter) {
        this.parameter = parameter;
    }

    public BlueprintHandler getBlueprintHandler() {
        return (BlueprintHandler) parameter.getNode().getCanvas().getHandler();
    }

    @Override
    public NodeParameter getParameter() {
        return parameter;
    }

    public abstract GenericStorage getGenericStorage();
    public abstract Type getType();

    @Override
    public boolean acceptPairing(NodeParameter parameter, boolean asInput) {
        NodeParameterHandler handler = parameter.getHandler();
        if (handler instanceof JavaParameterHandler) {
            // asInput ? getType() is assignable from handler.getType() : handler.getType() is assignable from getType()
//            TypeUtils.toString(getType())
            GenericStorage genericStorage = getGenericStorage();
            return asInput ? TypeUtils.isAssignable(genericStorage.fillOut(getType()), genericStorage.fillOut(((JavaParameterHandler) handler).getType())) :
                    TypeUtils.isAssignable(genericStorage.fillOut(((JavaParameterHandler) handler).getType()), genericStorage.fillOut(getType()));
        }
        return false;
    }

    @Override
    public void onNodeLinked(NodeParameter other, boolean asInput) {
        NodeParameterHandler.super.onNodeLinked(other, asInput);
        NodeParameterHandler handler = other.getHandler();
        if (handler instanceof JavaParameterHandler) {
            GenericStorage source, target;
            if (asInput) {
                source = getGenericStorage();
                target = ((JavaParameterHandler) handler).getGenericStorage();
            } else {
                source = ((JavaParameterHandler) handler).getGenericStorage();
                target = getGenericStorage();
            }
            // ignore the duplicate element
            source.getLookUp().add(target);
            target.getLookUp().add(target);
        }
    }

    @Override
    public void onNodeUnlinked(NodeParameter other, boolean asInput) {
        NodeParameterHandler.super.onNodeUnlinked(other, asInput);
        NodeParameterHandler handler = other.getHandler();
        if (handler instanceof JavaParameterHandler) {
            GenericStorage source, target;
            if (asInput) {
                source = getGenericStorage();
                target = ((JavaParameterHandler) handler).getGenericStorage();
            } else {
                source = ((JavaParameterHandler) handler).getGenericStorage();
                target = getGenericStorage();
            }
            // will only remove one
            source.getLookUp().remove(target);
            target.getLookUp().remove(target);
        }
    }
}
