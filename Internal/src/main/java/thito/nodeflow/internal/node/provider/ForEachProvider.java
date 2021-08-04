package thito.nodeflow.internal.node.provider;

import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.internal.node.*;
import thito.reflectedbytecode.*;
import thito.reflectedbytecode.jvm.*;

import java.util.*;

public class ForEachProvider extends AbstractNodeProvider {
    public ForEachProvider(NodeProviderCategory category) {
        super("java#foreach", "For Each Element", category);
        addParameter(new ExecutionNodeParameter("Execution", LinkMode.MULTIPLE, LinkMode.SINGLE));
        addParameter(new JavaNodeParameter("Iterable", Iterable.class, true, LinkMode.SINGLE, LinkMode.NONE));
        addParameter(new ExecutionNodeParameter("Do", LinkMode.NONE, LinkMode.SINGLE));
        addParameter(new JavaNodeParameter("Index", int.class, true, LinkMode.NONE, LinkMode.MULTIPLE));
        addParameter(new JavaNodeParameter("Element", Iterable.class.getTypeParameters()[0], true, LinkMode.NONE, LinkMode.SINGLE));
    }

    @Override
    public CompileSession createNewSession() {
        return new CompileSession() {
            @Override
            protected void prepareStructure() {
                getHandler().prepareLocalField(this, getNode().getParameter(1));
                getHandler().computeField(getNode().getParameter(3));
                getHandler().prepareStructure(findOutputNode(getNode().getParameter(0)));
                getHandler().prepareStructure(findOutputNode(getNode().getParameter(2)));
            }

            @Override
            protected void handleCompile() {
                Java.Loop(aWhile -> {
                    ILocalField iterator = getHandler().createLocalField(Iterator.class);
                    iterator.set(Java.Class(Iterable.class).getMethod("iterator").get().invoke(
                            getHandler().getReference(getNode().getParameter(1))
                    ));
                    getHandler().setReference(getNode().getParameter(3), 0);
                    Java.If(iterator.get().method("hasNext").invoke()).isTrue().Then(() -> {
                        getHandler().setReference(getNode().getParameter(4), iterator.get().method("next").invoke());
                        getHandler().compile(findOutputNode(getNode().getParameter(2)));
                        getHandler().setReference(getNode().getParameter(3),
                                Reference.javaToReference(getHandler().getReference(getNode().getParameter(3))).mathAdd(1));
                    }).Else(() -> {
                        aWhile.Break();
                    }).EndIf();
                });
                getHandler().compile(findOutputNode(getNode().getParameter(0)));
            }
        };
    }

    @Override
    public NodeCompileSession createCompileSession(Node node) {
        return new NodeCompileSession() {
            @Override
            public void handleCompile(NodeCompileHandler handler) {
                ILocalField element = handler.createLocalVariable(Java.Class(Iterable.class));
                handler.putReference(node.getParameter(3), element.get());
                element.set(null);
                ILocalField iterator = handler.createLocalVariable(Java.Class(Iterator.class));
                iterator.set(Java.Class(Iterable.class).getMethod("iterator").get()
                .invoke(handler.getReference((node.getParameter(1)))));
                IClass iteratorClass = Java.Class(Iterator.class);
                Java.Loop(aWhile -> {
                    Java.If(iteratorClass.getMethod("hasNext").get().invoke(iterator.get())).isTrue().Then(() -> {
                        element.set(iteratorClass.getMethod("next").get().invoke(iterator.get()));
                        handler.compile(findOutputNode(node.getParameter(2)));
                    }).Else(() -> {
                        aWhile.Break();
                    }).EndIf();
                });
                handler.compile(findOutputNode(node.getParameter(0)));
            }
        };
    }

}
