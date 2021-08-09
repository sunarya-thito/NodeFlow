package thito.nodeflow.internal.editor.record;

import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.internal.node.*;
import thito.nodeflow.internal.node.provider.*;

public class DefaultCreateRecordNodeProvider extends AbstractNodeProvider {
    private RecordFileModule module;
    public DefaultCreateRecordNodeProvider(RecordFileModule module, NodeProviderCategory category) {
        super("record://"+module.getName()+"/<init>()", "Create Record "+module.getName(), category);
        this.module = module;
        addParameter(new JavaNodeParameter("Record", module.getGenerated(), true, LinkMode.NONE, LinkMode.MULTIPLE));
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
                getHandler().setReference(getNode().getParameter(0),
                        getHandler().getNodeCompiler()
                                .getParent()
                                .getRecordFileCompiler()
                                .getCompiledDefaultConstructor().get(module.getFile().getPath())
                                .newInstance()
                );
            }
        };
    }

    @Override
    public NodeCompileSession createCompileSession(Node node) {
        return new NodeCompileSession() {
            @Override
            protected void handleCompile(NodeCompileHandler handler) {
                if (isParameterUsed(node.getParameter(0))) {
                    handler.putReference(node.getParameter(0),
                            ((NodeCompileHandlerImpl) handler).getCompiler()
                                    .getParent()
                                    .getRecordFileCompiler()
                            .getCompiledDefaultConstructor().get(module.getFile().getPath())
                            .newInstance()
                            );
                }
            }
        };
    }
}
