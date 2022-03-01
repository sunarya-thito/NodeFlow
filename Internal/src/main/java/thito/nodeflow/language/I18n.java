package thito.nodeflow.language;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class I18n extends SimpleStringProperty {

    public static I18n $(String key) {
        return Language.getLanguage().getItem(key);
    }

    public static I18n replace(String key) {
        List<I18n> format = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        StringBuilder keyBuilder = null;
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            if (keyBuilder != null) {
                if (c == '}') {
                    builder.append("%s");
                    format.add(I18n.$(keyBuilder.toString()));
                    keyBuilder = null;
                } else {
                    keyBuilder.append(c);
                }
            } else {
                if (c == '$') {
                    if (i + 1 < key.length() && key.charAt(i + 1) == '{') {
                        keyBuilder = new StringBuilder();
                        continue;
                    }
                }
                builder.append(c);
            }
        }
        return direct(builder.toString()).format(format.toArray());
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
        if (args.length == 0) return this;
        I18n text = new I18n();
        List<Observable> observables = new ArrayList<>(args.length + 1);
        observables.add(this);
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof Observable) {
                observables.add((Observable) args[i]);
                if (args[i] instanceof ObservableValue) {
                    args[i] = ((ObservableValue<?>) args[i]).getValue();
                }
            }
        }
        text.bind(Bindings.createStringBinding(() -> {
            String format = get();
            if (format == null) return null;
            return String.format(format, args);
        }, observables.toArray(new Observable[0])));
        return text;
    }

    public <T> ObservableValue<T> map(Function<String, T> stringTFunction) {
        return Bindings.createObjectBinding(() -> stringTFunction.apply(get()), this);
    }

}
