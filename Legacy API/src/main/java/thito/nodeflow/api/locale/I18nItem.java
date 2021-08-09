package thito.nodeflow.api.locale;

import javafx.beans.value.ObservableValue;

public interface I18nItem {

    String name();

    String getRawString();

    void setRawString(String rawString);

    String getString();

    String getString(Object... args);

    ObservableValue<String> stringBinding(Object... args);

    ObservableValue<String> stringBinding();

    default I18nItem format(Object... args) {
        I18nItem parent = this;
        return new I18nItem() {
            @Override
            public String name() {
                return parent.name();
            }

            @Override
            public String getRawString() {
                return parent.getRawString();
            }

            @Override
            public void setRawString(String rawString) {
                parent.setRawString(rawString);
            }

            @Override
            public String getString() {
                return parent.getString(args);
            }

            @Override
            public String getString(Object... args) {
                return parent.getString(args);
            }

            @Override
            public ObservableValue<String> stringBinding(Object... args) {
                return parent.stringBinding(args);
            }

            @Override
            public ObservableValue<String> stringBinding() {
                return parent.stringBinding(args);
            }
        };
    }

}
