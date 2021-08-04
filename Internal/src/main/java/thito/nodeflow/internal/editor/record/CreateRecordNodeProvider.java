package thito.nodeflow.internal.editor.record;

import thito.nodeflow.api.editor.node.Node;
import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.internal.node.*;
import thito.nodeflow.internal.node.provider.*;

import java.util.stream.*;

public class CreateRecordNodeProvider extends AbstractNodeProvider {
    private RecordFileModule module;
    public CreateRecordNodeProvider(RecordFileModule module, NodeProviderCategory category) {
        super("record://<init>("+module.getItems().stream().map(x -> x.getId().toString()).collect(Collectors.joining(","))+")", "Create Record "+module.getName(), category);
        this.module = module;
        addParameter(new JavaNodeParameter("Record", module.getGenerated(), true, LinkMode.NONE, LinkMode.MULTIPLE));
        for (RecordItem item : module.getItems()) {
            addParameter(new JavaNodeParameter(item.getName(), item.getType(), false, LinkMode.SINGLE, LinkMode.NONE));
        }
    }

    @Override
    public CompileSession createNewSession() {
        return new CompileSession() {
            @Override
            protected void prepareStructure() {
                getHandler().computeField(getNode().getParameter(0));
                for (int i = 0; i < module.getItems().size(); i++) {
                    getHandler().prepareLocalField(this, getNode().getParameter(i + 1));
                }
            }

            @Override
            protected void handleCompile() {
                getHandler().setReference(getNode().getParameter(0),
                        getHandler().getNodeCompiler().getParent().getRecordFileCompiler()
                                .getCompiledConstructor().get(module.getFile().getPath())
                                .newInstance(IntStream.range(0, module.getItems().size()).mapToObj(i -> getNode().getParameter(i + 1)))
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
                            ((NodeCompileHandlerImpl) handler).getCompiler().getParent().getRecordFileCompiler()
                            .getCompiledConstructor().get(module.getFile().getPath())
                            .newInstance(IntStream.range(0, module.getItems().size()).mapToObj(i -> node.getParameter(i + 1)))
                            );
                }
            }
        };
    }
}
