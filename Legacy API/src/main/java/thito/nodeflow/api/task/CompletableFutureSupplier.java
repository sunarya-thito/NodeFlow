package thito.nodeflow.api.task;

public interface CompletableFutureSupplier<T> extends FutureSupplier<T> {
    void complete(T value);

    void error(Throwable error);

    void reset();

    boolean isComplete();
}
