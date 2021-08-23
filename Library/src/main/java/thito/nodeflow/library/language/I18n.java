package thito.nodeflow.library.language;

import javafx.beans.Observable;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.beans.value.*;

import java.util.*;
import java.util.function.*;

public class I18n extends SimpleStringProperty {

    public static I18n $(String key) {
        return Language.getLanguage().getItem(key);
    }

    public static I18n direct(String direct) {
        return new I18n(direct);
    }

    public I18n() {
    }

    public I18n(String s) {
        super(s);
    }

    public I18n format(Object...args) {
        I18n text = new I18n();
        List<Observable> observables = new ArrayList<>(args.length + 1);
        Arrays.stream(args).filter(x -> x instanceof Observable).map(Observable.class::cast).forEach(observables::add);
        observables.add(this);
        text.bind(Bindings.createStringBinding(this::get, observables.toArray(new Observable[0])));
        return text;
    }

    public <T> ObservableValue<T> map(Function<String, T> stringTFunction) {
        return Bindings.createObjectBinding(() -> stringTFunction.apply(get()), this);
    }

}
