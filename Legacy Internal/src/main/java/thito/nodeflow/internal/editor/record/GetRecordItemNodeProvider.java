package thito.nodeflow.internal.editor.record;

import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.internal.node.*;
import thito.nodeflow.internal.node.provider.*;

public class GetRecordItemNodeProvider extends AbstractNodeProvider {
    private RecordItem item;
    public GetRecordItemNodeProvider(RecordItem item, NodeProviderCategory category) {
        super("record://set/"+item.getId(), "Get \""+item.getName()+"\" Value", category);
        addParameter(new JavaNodeParameter("Target", item.getModule().getGenerated(), true, LinkMode.SINGLE, LinkMode.NONE));
        addParameter(new JavaNodeParameter("Value", item.getType(), false, LinkMode.NONE, LinkMode.MULTIPLE));
        this.item = item;
    }

    @Override
    public CompileSession createNewSession() {
        return new CompileSession() {
            @Override
            protected void prepareStructure() {
                getHandler().prepareLocalField(this, getNode().getParameter(0));
                getHandler().computeField(getNode().getParameter(1));
            }

            @Override
            protected void handleCompile() {
                getHandler().setReference(getNode().getParameter(1),
                        getHandler().getNodeCompiler().getParent().getRecordFileCompiler().getCompiledField().get(item.getId())
                                .get(getHandler().getReference(getNode().getParameter(0))));
            }
        };
    }

    @Override
    public NodeCompileSession createCompileSession(Node node) {
        return new NodeCompileSession() {
            @Override
            protected void handleCompile(NodeCompileHandler handler) {
                if (isParameterUsed(node.getParameter(1))) {
                    handler.putReference(node.getParameter(1),
                            ((NodeCompileHandlerImpl) handler).getCompiler().getParent().getRecordFileCompiler().getCompiledField().get(item.getId())
                                    .get(handler.getReference(node.getParameter(0))));
                }
            }
        };
    }
}
