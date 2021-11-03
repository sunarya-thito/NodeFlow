package thito.nodeflow.internal.binding;

import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.collections.*;

import java.util.function.*;

public class MappedBinding<F,T> extends ObjectBinding<T> {

    public static <K, V> ObjectBinding<V> map(ObservableValue<K> source, Function<K, V> mapper) {
        return new MappedBinding<>(source, mapper);
    }

    public static <K, V, T> ObjectBinding<V> flatMap(ObservableValue<K> source, Function<K, ObservableValue<V>> flatMapper) {
        ObjectProperty<ObservableValue<V>> listener = new SimpleObjectProperty<>();
        MappedBinding<K, V> mappedBinding = new MappedBinding<>(source, value -> {
            ObservableValue<V> mapped = flatMapper.apply(value);
            listener.set(mapped);
            return mapped == null ? null : mapped.getValue();
        });
        listener.addListener((obs, old, val) -> {
            if (old != null) {
                mappedBinding.unbind(old);
            }
            if (val != null) {
                mappedBinding.bind(val);
            }
        });
        return mappedBinding;
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
