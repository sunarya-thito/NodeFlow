package thito.nodeflow.internal.node.provider.minecraft;

import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.node.*;
import thito.nodeflow.api.node.state.*;
import thito.nodeflow.internal.node.*;
import thito.nodeflow.internal.node.headless.*;
import thito.nodeflow.internal.node.provider.*;
import thito.nodeflow.internal.node.state.*;

public class ChestMenuProvider extends AbstractNodeProvider {
    public ChestMenuProvider(NodeProviderCategory category) {
        super("minecraft.chestMenu", "Menu", category);
    }

    @Override
    public Node createComponent(NodeModule module) {
        if (module instanceof HeadlessNodeModule) {

        } else {
            ComponentStateImpl state = new ComponentStateImpl((StandardNodeModule) module);

        }
        return super.createComponent(module);
    }

    @Override
    public NodeCompileSession createCompileSession(Node node) {
        return null;
    }

    @Override
    public CompileSession createNewSession() {
        return null;
    }
}
