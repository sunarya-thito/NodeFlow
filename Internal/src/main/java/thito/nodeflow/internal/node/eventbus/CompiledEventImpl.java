package thito.nodeflow.internal.node.eventbus;

import thito.nodeflow.api.node.eventbus.*;
import thito.nodeflow.internal.node.headless.*;
import thito.reflectedbytecode.*;

public class CompiledEventImpl implements CompiledEvent {
    private GMethod method;
    private HeadlessEventNode node;
    private EventProvider provider;

    public CompiledEventImpl(GMethod method, HeadlessEventNode node, EventProvider provider) {
        this.method = method;
        this.node = node;
        this.provider = provider;
    }

    @Override
    public EventNode getNode() {
        return node;
    }

    @Override
    public EventProvider getProvider() {
        return provider;
    }

    @Override
    public void call(Object... args) {
        Reference reference = method.getDeclaringClass().newInstance();
        method.invoke(reference, args);
    }
}
