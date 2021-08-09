package thito.nodeflow.internal.editor.config;

import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.internal.node.*;
import thito.nodeflow.internal.node.provider.*;
import thito.reflectedbytecode.*;

import java.net.*;
import java.util.*;

public class SetConfigNodeProvider extends AbstractNodeProvider {

    private static String encode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    private UUID id;
    public SetConfigNodeProvider(UUID id, String name, Class<?> type, NodeProviderCategory category) {
        super("config://set/"+id, "Set \""+name+"\" Value", category);
        this.id = id;
        addParameter(new ExecutionNodeParameter("Execution", LinkMode.MULTIPLE, LinkMode.SINGLE));
        addParameter(new JavaNodeParameter("Value", type, false, LinkMode.SINGLE, LinkMode.NONE));
    }

    @Override
    public CompileSession createNewSession() {
        return new CompileSession() {
            @Override
            protected void prepareStructure() {
                getHandler().prepareLocalField(this, getNode().getParameter(1));
                getHandler().prepareStructure(findOutputNode(getNode().getParameter(0)));
            }

            @Override
            protected void handleCompile() {
                getHandler().getNodeCompiler().getParent().getConfigFileCompiler().setReference(id, Reference.javaToReference(getHandler().getReference(getNode().getParameter(1))));
                getHandler().compile(findOutputNode(getNode().getParameter(0)));
            }
        };
    }

    @Override
    public NodeCompileSession createCompileSession(Node node) {
        return new NodeCompileSession() {
            @Override
            protected void handleCompile(NodeCompileHandler handler) {
                ((NodeCompileHandlerImpl) handler).getCompiler().getParent().getConfigFileCompiler().setReference(id, handler.getReference(node.getParameter(1)));
                handler.compile(findOutputNode(node.getParameter(0)));
            }
        };
    }
}
