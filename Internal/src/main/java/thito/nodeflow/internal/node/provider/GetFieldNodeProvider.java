package thito.nodeflow.internal.node.provider;

import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.internal.node.*;
import thito.reflectedbytecode.*;

import java.lang.reflect.*;

public class GetFieldNodeProvider extends AbstractNodeProvider implements ClassMemberProvider {
    private Field field;

    public GetFieldNodeProvider(Field field, NodeProviderCategory category) {
        super("get_field://"+field.getDeclaringClass().getName()+"#"+field.getName(), "Get "+ JavaNodeProviderCategory.capitalizeCamelCase(field.getName()), category);
        this.field = field;
        if (!Modifier.isStatic(field.getModifiers())) {
            addParameter(new JavaNodeParameter("Source", field.getDeclaringClass(), false, LinkMode.SINGLE, LinkMode.NONE));
        }
        addParameter(new JavaNodeParameter("Result", field.getGenericType(), true, LinkMode.NONE, LinkMode.MULTIPLE));
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
                if (!Modifier.isStatic(field.getModifiers())) {
                    getHandler().prepareLocalField(this, getNode().getParameter(0));
                    getHandler().computeField(getNode().getParameter(1));
                } else {
                    getHandler().computeField(getNode().getParameter(0));
                }
            }

            @Override
            protected void handleCompile() {
                if (Modifier.isStatic(field.getModifiers())) {
                    getHandler().setReference(getNode().getParameter(0),
                            new KField(field).get(null));
                } else {
                    getHandler().setReference(getNode().getParameter(1),
                            new KField(field).get(getHandler().getReference(getNode().getParameter(0))));
                }
            }
        };
    }

    @Override
    public NodeCompileSession createCompileSession(Node node) {
        return new NodeCompileSession() {
            @Override
            public void handleCompile(NodeCompileHandler handler) {
                if (Modifier.isStatic(field.getModifiers())) {
                    if (isParameterUsed(node.getParameter(0))) {
                        System.out.println("PUT STATIC REFERENCE: "+field);
                        handler.putReference(node.getParameter(0), new KField(field).get(null));
                    }
                } else {
                    if (isParameterUsed(node.getParameter(1))) {
                        System.out.println("PUT REFERENCE: "+field);
                        handler.putReference(node.getParameter(1), new KField(field).get(handler.getReference(
                                (node.getParameter(0))
                        )));
                    }
                }
            }
        };
    }

}
