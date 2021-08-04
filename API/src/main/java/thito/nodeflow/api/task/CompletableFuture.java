package thito.nodeflow.api.task;

public interface CompletableFuture extends Future {
    void complete();

    void error(Throwable t);

    void reset();

    boolean isComplete();
}
