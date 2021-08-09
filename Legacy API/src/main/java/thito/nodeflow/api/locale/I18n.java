package thito.nodeflow.api.locale;

import javafx.beans.Observable;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.beans.value.*;
import thito.nodeflow.api.NodeFlow;

import java.util.*;
import java.util.stream.*;

public interface I18n {
    static I18nItem $(String name) {
        return NodeFlow.getApplication().getLocaleManager().getItem(name);
    }
    static I18nItem direct(String value) {
        return new I18nItem() {
            private StringProperty property = new SimpleStringProperty(value);
            @Override
            public String name() {
                return "";
            }

            @Override
            public String getRawString() {
                return value;
            }

            @Override
            public void setRawString(String rawString) {
            }

            @Override
            public String getString() {
                return value;
            }

            @Override
            public String getString(Object... args) {
                return String.format(value, Arrays.stream(args).map(x -> x instanceof ObservableValue ? ((ObservableValue<?>) x).getValue() : x).toArray());
            }

            @Override
            public ObservableValue<String> stringBinding(Object... args) {
                Object[] arguments = Arrays.stream(args).map(x -> x instanceof ObservableValue ? ((ObservableValue<?>) x).getValue() : x).toArray();
                ArrayList<javafx.beans.Observable> observables = new ArrayList<>(Arrays.stream(args).filter(javafx.beans.Observable.class::isInstance).map(javafx.beans.Observable.class::cast).collect(Collectors.toList()));
                observables.add(property);
                return Bindings.createStringBinding(() -> String.format(getRawString(), arguments),
                        observables.toArray(new Observable[0])
                );
            }

            @Override
            public ObservableValue<String> stringBinding() {
                return property;
            }
        };
    }
    static Locale impl_getLocalePeer() {
        return Locale.forLanguageTag($("code").getString());
    }
}
