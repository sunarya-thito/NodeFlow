package thito.nodeflow.api.node;

import thito.nodeflow.api.task.FutureSupplier;

public interface NodeDocumentation {
    FutureSupplier<String> getName();

    FutureSupplier<String> getDescription();
}
