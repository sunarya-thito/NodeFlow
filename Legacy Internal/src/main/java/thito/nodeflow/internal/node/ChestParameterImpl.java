package thito.nodeflow.internal.node;

import thito.nodeflow.api.*;
import thito.nodeflow.api.editor.node.ChestSlot;
import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.node.state.*;
import thito.nodeflow.minecraft.*;
import thito.nodejfx.parameter.type.*;

public class ChestParameterImpl extends NodeParameterImpl implements ChestParameter {

    public ChestParameterImpl(ComponentParameterState state, String name, Node node) {
        super(state, name, node, param -> new ChestNodeParameter(),
                new CompoundType().scan(
                        NodeFlow.getApplication().getBundleManager().findClass("nodeflow.spigotmc.ChestInterface")), NodeFlow.getApplication().getBundleManager().findClass("nodeflow.spigotmc.ChestInterface"));
    }

    @Override
    public ChestNodeParameter impl_getPeer() {
        return (ChestNodeParameter) super.impl_getPeer();
    }

    @Override
    public ChestSlot[] getSlots() {
        return new ChestSlot[0];
    }

    @Override
    public void setRows(int rows) {

    }

    @Override
    public int getRows() {
        return 0;
    }

    @Override
    public void setTitle(String title) {

    }

    @Override
    public String getTitle() {
        return null;
    }
}
