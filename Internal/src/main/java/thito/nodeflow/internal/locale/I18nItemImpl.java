package thito.nodeflow.internal.locale;

import javafx.beans.Observable;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.beans.value.*;
import thito.nodeflow.api.locale.*;

import java.util.*;
import java.util.stream.*;

public class I18nItemImpl implements I18nItem {

    public static I18nItem fromBinding(ObservableValue<String> value) {
        return new I18nItem() {

            @Override
            public String name() {
                return "binding-"+value.hashCode();
            }

            @Override
            public String getRawString() {
                return value.getValue();
            }

            @Override
            public void setRawString(String rawString) {
                throw new UnsupportedOperationException();
            }

            @Override
            public String getString() {
                return value.getValue();
            }

            @Override
            public String getString(Object... args) {
                return String.format(value.getValue(), Arrays.stream(args).map(x -> x instanceof ObservableValue ? ((ObservableValue<?>) x).getValue() : x).toArray());
            }

            @Override
            public ObservableValue<String> stringBinding(Object... args) {
                Object[] arguments = Arrays.stream(args).map(x -> x instanceof ObservableValue ? ((ObservableValue<?>) x).getValue() : x).toArray();
                ArrayList<Observable> observables = new ArrayList<>(Arrays.stream(args).filter(Observable.class::isInstance).map(Observable.class::cast).collect(Collectors.toList()));
                observables.add(value);
                return Bindings.createStringBinding(() -> String.format(getRawString(), arguments),
                        observables.toArray(new Observable[0])
                        );
            }

            @Override
            public ObservableValue<String> stringBinding() {
                return value;
            }
        };
    }

    private String name;
    private StringProperty rawString = new SimpleStringProperty("");

    public I18nItemImpl(String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public void setRawString(String rawString) {
        this.rawString.set(rawString == null ? "" : rawString);
    }

    @Override
    public String getRawString() {
        return rawString.get();
    }

    @Override
    public String getString(Object... args) {
        return String.format(getRawString(), Arrays.stream(args).map(x -> x instanceof ObservableValue ? ((ObservableValue<?>) x).getValue() : x).toArray());
    }

    @Override
    public ObservableValue<String> stringBinding(Object... args) {
        return Bindings.createStringBinding(() -> {
                    Object[] arguments = Arrays.stream(args).map(x -> x instanceof ObservableValue ? ((ObservableValue<?>) x).getValue() : x).toArray();
                    return String.format(getRawString(), arguments);
                },
                Arrays.stream(args).filter(Observable.class::isInstance).toArray(Observable[]::new));
    };

    @Override
    public String getString() {
        return getRawString();
    }

    @Override
    public ObservableValue<String> stringBinding() {
        return rawString;
    }

    @Override
    public String toString() {
        return getRawString();
    }
}
