package thito.nodeflow.internal.editor.config;

import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.internal.node.*;
import thito.nodeflow.internal.node.provider.*;

import java.util.*;

public class GetConfigNodeProvider extends AbstractNodeProvider {

    private Class<?> type;
    private UUID uuid;
    public GetConfigNodeProvider(UUID uuid, String name, Class<?> type, NodeProviderCategory category) {
        super("config://get/"+uuid, "Get \""+name+"\" Value", category);
        this.uuid = uuid;
        this.type = type;
        addParameter(new JavaNodeParameter("Value", type, true, LinkMode.NONE, LinkMode.MULTIPLE));
    }

    @Override
    public CompileSession createNewSession() {
        return new CompileSession() {
            @Override
            protected void prepareStructure() {
                getHandler().computeField(getNode().getParameter(0));
            }

            @Override
            protected void handleCompile() {
                getHandler().setReference(getNode().getParameter(0), getHandler().getNodeCompiler().getParent().getConfigFileCompiler().getReference(uuid));
            }
        };
    }

    @Override
    public NodeCompileSession createCompileSession(Node node) {
        return new NodeCompileSession() {
            @Override
            protected void handleCompile(NodeCompileHandler handler) {
                if (isParameterUsed(node.getParameter(0))) {
                    handler.putReference(node.getParameter(0), ((NodeCompileHandlerImpl) handler).getCompiler().getParent().getConfigFileCompiler().getReference(uuid));
                }
            }
        };
    }
}
