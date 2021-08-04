package thito.nodeflow.library.binding;

import javafx.beans.*;
import javafx.beans.property.*;
import javafx.beans.value.*;

import java.lang.ref.*;
import java.util.function.*;

public class MappedBidirectionalBinding<F, T> implements WeakListener {

    public static <F, T> void bindBidirectional(Property<F> a, Property<T> b, Function<F, T> from, Function<T, F> to) {
        a.setValue(to.apply(b.getValue()));
        new MappedBidirectionalBinding<>(a, b, from, to);
    }

    private WeakReference<Property<F>> aProp;
    private WeakReference<Property<T>> bProp;

    private Function<F, T> aFunc;
    private Function<T, F> bFunc;

    private boolean updating;

    public MappedBidirectionalBinding(Property<F> a, Property<T> b, Function<F, T> from, Function<T, F> to) {
        aProp = new WeakReference<>(a);
        bProp = new WeakReference<>(b);
        aFunc = from;
        bFunc = to;
        a.addListener(new ValueListener<>(from, b));
        b.addListener(new ValueListener<>(to, a));
    }

    @Override
    public boolean wasGarbageCollected() {
        return aProp.get() == null || bProp.get() == null;
    }

    public class ValueListener<X, Y> implements WeakListener, ChangeListener<X> {

        private Function<X, Y> map;
        private Property<Y> property;

        public ValueListener(Function<X, Y> map, Property<Y> property) {
            this.map = map;
            this.property = property;
        }

        @Override
        public boolean wasGarbageCollected() {
            return MappedBidirectionalBinding.this.wasGarbageCollected();
        }

        @Override
        public void changed(ObservableValue<? extends X> observable, X oldValue, X newValue) {
            if (!updating) {
                updating = true;
                property.setValue(map.apply(newValue));
                updating = false;
            }
        }

    }

}
