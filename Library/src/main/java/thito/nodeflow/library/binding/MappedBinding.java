package thito.nodeflow.library.binding;

import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.collections.*;

import java.util.function.*;

public class MappedBinding<F,T> extends ObjectBinding<T> {

    public static <K, V> ObjectBinding<V> map(ObservableValue<K> source, Function<K, V> mapper) {
        return new MappedBinding<>(source, mapper);
    }

    private ObservableValue<F> source;
    private Function<F, T> mapper;

    public MappedBinding(ObservableValue<F> source, Function<F, T> mapper) {
        this.source = source;
        this.mapper = mapper;
        bind(source);
    }

    @Override
    public ObservableList<?> getDependencies() {
        return FXCollections.singletonObservableList(source);
    }

    @Override
    protected T computeValue() {
        return mapper.apply(source.getValue());
    }

}
