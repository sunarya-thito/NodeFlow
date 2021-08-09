package thito.nodeflow.internal.node;

import thito.nodeflow.api.editor.node.*;
import thito.reflectedbytecode.*;

public abstract class CompileSession {
    Node node;
    CompileHandler handler;
    MemberBodyAccessor accessor;

    public Node getNode() {
        return node;
    }

    public CompileHandler getHandler() {
        return handler;
    }

    protected void Return() {
        accessor.doneReturn();
    }

    protected void Return(Object object) {
        ((GMethodAccessor) accessor).doneReturn(object);
    }

    protected void compile(MemberBodyAccessor accessor) {
        this.accessor = accessor;
        handleCompile();
//        Code.getCode().getCodeVisitor().visit
//        Java.Try(() -> {
//
//        }).Catch(Throwable.class).Caught(error -> {
//            String id = node.getState().getID().toString();
//            Java.Throw(Java.Class(RuntimeException.class).getConstructor(String.class, Throwable.class)
//                    .get().newInstance("!!NodeException: "+id+" ("+node.getModule().getFile().getFileName()+")",
//                            error));
//        });
    }

    protected abstract void prepareStructure();
    protected abstract void handleCompile();
}
