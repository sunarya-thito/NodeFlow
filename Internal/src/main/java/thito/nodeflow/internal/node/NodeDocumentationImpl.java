package thito.nodeflow.internal.node;

import thito.nodeflow.api.bundle.java.docs.*;
import thito.nodeflow.api.node.*;
import thito.nodeflow.api.task.*;

public class NodeDocumentationImpl implements NodeDocumentation {
    private MemberJavaDoc javaDoc;

    public NodeDocumentationImpl(MemberJavaDoc javaDoc) {
        this.javaDoc = javaDoc;
    }

    @Override
    public FutureSupplier<String> getName() {
        return FutureSupplier.createDirect(javaDoc == null ? null : javaDoc.getName());
    }

    @Override
    public FutureSupplier<String> getDescription() {
        return FutureSupplier.createDirect(javaDoc == null ? null : javaDoc.getDescription());
    }
}
