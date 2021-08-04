package thito.nodeflow.api.task;

import thito.nodeflow.api.NodeFlow;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface FutureSupplier<T> {
    FutureSupplier NULL = createDirect(null);

    static <T> FutureSupplier<T> createDirect(T value) {
        return new FutureSupplier<T>() {
            @Override
            public FutureSupplier<T> andThen(Consumer<T> consumer) {
                consumer.accept(value);
                return this;
            }

            @Override
            public FutureSupplier<T> andThenError(Consumer<Throwable> error) {
                return this;
            }
        };
    }

    static <T> FutureSupplier<T> executeOnBackground(Supplier<T> supplier) {
        return create(NodeFlow.getApplication().getTaskManager().getBackgroundThread(), supplier);
    }

    static <T> FutureSupplier<T> create(TaskThread thread, Supplier<T> supplier) {
        return NodeFlow.getApplication().getTaskManager().createFutureSupplier(thread, supplier);
    }

    static <T> CompletableFutureSupplier<T> createCompletable() {
        return NodeFlow.getApplication().getTaskManager().createCompletableFutureSupplier();
    }

    FutureSupplier<T> andThen(Consumer<T> consumer);

    FutureSupplier<T> andThenError(Consumer<Throwable> error);
}
