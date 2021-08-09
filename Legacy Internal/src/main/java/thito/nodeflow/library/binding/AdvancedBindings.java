package thito.nodeflow.library.binding;

import javafx.beans.property.*;
import javafx.beans.value.*;

import java.util.concurrent.atomic.*;
import java.util.function.*;

public class AdvancedBindings {

    public static <Y extends WritableValue<T> & ObservableValue<T>, T> void lock(Y value, T lockedValue) {
        AtomicBoolean updating = new AtomicBoolean();
        value.setValue(lockedValue);
        value.addListener((obs, old, val) -> {
            if (updating.get()) return;
            updating.set(true);
            value.setValue(lockedValue);
            updating.set(false);
        });
    }

    public static <T> void bindFilterBidirectional(Property<T> property, Property<T> other, BiFunction<T, T, T> filter) {
        AtomicBoolean updating = new AtomicBoolean();
        if (property.getValue() != other.getValue()) {
            other.setValue(property.getValue());
        }
        other.addListener((obs, old, val) -> {
            if (updating.get()) return;
            updating.set(true);
            val = filter.apply(old, val);
            other.setValue(val);
            property.setValue(val);
            updating.set(false);
        });
        property.addListener((obs, old, val) -> {
            if (updating.get()) return;
            updating.set(true);
            val = filter.apply(old, val);
            other.setValue(val);
            property.setValue(val);
            updating.set(false);
        });
    }
    public static <T> void bindFilterBidirectional(Property<T> property, Property<T> other, Function<T, T> filter) {
        AtomicBoolean updating = new AtomicBoolean();
        if (property.getValue() != other.getValue()) {
            other.setValue(property.getValue());
        }
        other.addListener((obs, old, val) -> {
            if (updating.get()) return;
            updating.set(true);
            val = filter.apply(val);
            other.setValue(val);
            property.setValue(val);
            updating.set(false);
        });
        property.addListener((obs, old, val) -> {
            if (updating.get()) return;
            updating.set(true);
            val = filter.apply(val);
            other.setValue(val);
            property.setValue(val);
            updating.set(false);
        });
    }

    public static <T> void bindBidirectional(Property<T> property, ObservableValue<T> value, Consumer<T> setter) {
        AtomicBoolean updating = new AtomicBoolean();
        if (value.getValue() != property.getValue()) {
            property.setValue(value.getValue());
        }
        value.addListener((obs, old, val) -> {
            if (updating.get()) return;
            updating.set(true);
            property.setValue(val);
            updating.set(false);
        });
        property.addListener((obs, old, val) -> {
            if (updating.get()) return;
            updating.set(true);
            setter.accept(val);
            updating.set(false);
        });
    }
}
