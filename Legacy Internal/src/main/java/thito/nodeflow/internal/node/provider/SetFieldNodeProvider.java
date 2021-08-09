package thito.nodeflow.internal.node.provider;

import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.internal.node.*;
import thito.reflectedbytecode.*;

import java.lang.reflect.*;

public class SetFieldNodeProvider extends AbstractNodeProvider implements ClassMemberProvider {
    private Field field;

    public SetFieldNodeProvider(Field field, NodeProviderCategory category) {
        super("set_field://"+field.getDeclaringClass().getName()+"#"+field.getName(), "Set "+ JavaNodeProviderCategory.capitalizeCamelCase(field.getName()), category);
        this.field = field;
        addParameter(new ExecutionNodeParameter("Execution", LinkMode.MULTIPLE, LinkMode.SINGLE));
        if (!Modifier.isStatic(field.getModifiers())) {
            addParameter(new JavaNodeParameter("Owner", field.getDeclaringClass(), false, LinkMode.SINGLE, LinkMode.NONE));
        }
        addParameter(new JavaNodeParameter("Value", field.getGenericType(), false, LinkMode.SINGLE, LinkMode.NONE));
    }

    public Field getField() {
        return field;
    }

    @Override
    public Member getMember() {
        return field;
    }

    @Override
    public CompileSession createNewSession() {
        return new CompileSession() {
            @Override
            protected void prepareStructure() {
                getHandler().prepareLocalField(this, getNode().getParameter(1));
                if (!Modifier.isStatic(field.getModifiers())) {
                    getHandler().prepareLocalField(this, getNode().getParameter(2));
                }
                getHandler().prepareStructure(findOutputNode(getNode().getParameter(0)));
            }

            @Override
            protected void handleCompile() {
                if (Modifier.isStatic(field.getModifiers())) {
                    new KField(field)
                            .set(getHandler().getReference((getNode().getParameter(1))),
                                    getHandler().getReference((getNode().getParameter(2))));
                } else {
                    new KField(field)
                            .set(null,
                                    getHandler().getReference((getNode().getParameter(1))));
                }
                getHandler().compile(findOutputNode(getNode().getParameter(0)));
            }
        };
    }

    @Override
    public NodeCompileSession createCompileSession(Node node) {
        return new NodeCompileSession() {
            @Override
            public void handleCompile(NodeCompileHandler handler) {
                if (Modifier.isStatic(field.getModifiers())) {
                    new KField(field)
                            .set(handler.getReference((node.getParameter(1))),
                                    handler.getReference((node.getParameter(2))));
                } else {
                    new KField(field)
                            .set(null,
                                    handler.getReference((node.getParameter(1))));
                }
                handler.compile(findOutputNode(node.getParameter(0)));
            }
        };
    }

}
