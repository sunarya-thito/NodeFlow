package thito.nodeflow.internal.node.provider;

import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.internal.node.*;
import thito.reflectedbytecode.*;
import thito.reflectedbytecode.jvm.*;

import java.lang.reflect.*;

public class ForEachArrayProvider extends AbstractNodeProvider {
    private TypeVariable type = new TypeVariableImpl("T");
    public ForEachArrayProvider(NodeProviderCategory category) {
        super("java#foreacharray://", "For Each (Array) Element", category);
        addParameter(new ExecutionNodeParameter("Execution", LinkMode.MULTIPLE, LinkMode.SINGLE));
        addParameter(new JavaNodeParameter("Array", new GenericArrayTypeImpl(type), true, LinkMode.SINGLE, LinkMode.NONE));
        addParameter(new ExecutionNodeParameter("Do", LinkMode.NONE, LinkMode.SINGLE));
        addParameter(new JavaNodeParameter("Index", int.class, true, LinkMode.NONE, LinkMode.SINGLE));
        addParameter(new JavaNodeParameter("Element", type, true, LinkMode.NONE, LinkMode.SINGLE));
    }

    @Override
    public CompileSession createNewSession() {
        return new CompileSession() {
            @Override
            protected void prepareStructure() {
                getHandler().prepareLocalField(this, getNode().getParameter(1));
                getHandler().computeField(getNode().getParameter(3));
                getHandler().computeField(getNode().getParameter(4));
                getHandler().prepareStructure(findOutputNode(getNode().getParameter(2)));
                getHandler().prepareStructure(findOutputNode(getNode().getParameter(0)));
            }

            @Override
            protected void handleCompile() {
                getHandler().setReference(getNode().getParameter(3), 0);
                Java.Loop(aWhile -> {
                    Java.If(getHandler().getReference(getNode().getParameter(3)))
                            .isLessThan(Java.ArrayLength(getHandler().getReference(getNode().getParameter(1)))).Then(() -> {
                        getHandler().setReference(getNode().getParameter(4),
                                Java.GetArrayElement(getHandler().getReference(getNode().getParameter(1)),
                                        getHandler().getReference(getNode().getParameter(3))));
                        getHandler().compile(findOutputNode(getNode().getParameter(2)));
                        getHandler().setReference(getNode().getParameter(3), Java.Math.add(
                                getHandler().getReference(getNode().getParameter(3)), 1
                        ));
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
                ILocalField element = handler.createLocalVariable(Java.Class(Object.class));
                handler.putReference(node.getParameter(3), element.get());
                element.set(null);
                ILocalField index = handler.createLocalVariable(Java.Class(int.class));
                index.set(0);
                Reference array = handler.getReference((node.getParameter(1)));
                Java.Loop(aWhile -> {
                    Java.If(index.get()).isLessThan(Java.ArrayLength(array)).Then(() -> {
                        element.set(array.arrayGet(index.get()));
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
