package thito.nodeflow.internal.settings;

import javafx.beans.property.*;
import javafx.beans.value.*;
import thito.nodeflow.library.language.*;

public class SettingsProperty<T> extends SimpleObjectProperty<T> {
    private I18n name;

    public SettingsProperty(I18n name, T initialValue) {
        this.name = name;
    }

    public <K> K value() {
        return (K) get();
    }

    public void addListenerAndFire(ChangeListener<? super T> listener) {
        listener.changed(this, null, get());
        addListener(listener);
    }
}
