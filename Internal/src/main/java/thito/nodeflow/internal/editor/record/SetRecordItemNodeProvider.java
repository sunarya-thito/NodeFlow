package thito.nodeflow.internal.editor.record;

import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.internal.node.*;
import thito.nodeflow.internal.node.provider.*;

public class SetRecordItemNodeProvider extends AbstractNodeProvider {
    private RecordItem item;
    public SetRecordItemNodeProvider(RecordItem item, NodeProviderCategory category) {
        super("record://set/"+item.getId(), "Set \""+item.getName()+"\" Value", category);
        addParameter(new ExecutionNodeParameter("Execution", LinkMode.MULTIPLE, LinkMode.SINGLE));
        addParameter(new JavaNodeParameter("Target", item.getModule().getGenerated(), true, LinkMode.SINGLE, LinkMode.NONE));
        addParameter(new JavaNodeParameter("Value", item.getType(), false, LinkMode.SINGLE, LinkMode.NONE));
        this.item = item;
    }

    @Override
    public CompileSession createNewSession() {
        return new CompileSession() {
            @Override
            protected void prepareStructure() {
                getHandler().prepareLocalField(this, getNode().getParameter(1));
                getHandler().computeField(getNode().getParameter(2));
                getHandler().prepareStructure(findOutputNode(getNode().getParameter(0)));
            }

            @Override
            protected void handleCompile() {
                getHandler().getNodeCompiler().getParent().getRecordFileCompiler().getCompiledField().get(item.getId())
                        .set(getHandler().getReference(getNode().getParameter(1)), getHandler().getReference(getNode().getParameter(2)));
                getHandler().compile(findOutputNode(getNode().getParameter(0)));
            }
        };
    }

    @Override
    public NodeCompileSession createCompileSession(Node node) {
        return new NodeCompileSession() {
            @Override
            protected void handleCompile(NodeCompileHandler handler) {
                ((NodeCompileHandlerImpl) handler).getCompiler().getParent().getRecordFileCompiler().getCompiledField().get(item.getId())
                        .set(handler.getReference(node.getParameter(1)), handler.getReference(node.getParameter(2)));
                handler.compile(findOutputNode(node.getParameter(0)));
            }
        };
    }
}
