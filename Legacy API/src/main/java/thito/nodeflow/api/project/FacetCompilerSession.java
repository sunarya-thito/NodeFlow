package thito.nodeflow.api.project;

import thito.nodeflow.api.task.*;

import java.util.function.*;

public interface FacetCompilerSession {
    void linkedPreCompileTask(FacetCompilerHandler handler, CompletableFutureSupplier<Boolean> future, Runnable next);
    void linkedPostCompileTask(FacetCompilerHandler handler, CompletableFutureSupplier<Boolean> future, Runnable next);
}
