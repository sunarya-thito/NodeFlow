package thito.nodeflow.api.editor.node;

import thito.reflectedbytecode.jvm.*;

public abstract class NodeCompileSession {
    protected abstract void handleCompile(NodeCompileHandler handler);
    public void compile(NodeCompileHandler handler, Node node) {
        Java.Try(() -> {
            handleCompile(handler);
        }).Catch(Throwable.class).Caught(t -> {
            handler.doCleanUp();
            String id = node.getState().getID().toString();
            Java.Throw(Java.Class(RuntimeException.class).getConstructor(String.class, Throwable.class)
                    .get().newInstance("!!NodeException: "+id+" ("+node.getModule().getFile().getFileName()+")",
                            t));
        });
    }
}
