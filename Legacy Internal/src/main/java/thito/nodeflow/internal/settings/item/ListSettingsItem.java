package thito.nodeflow.internal.settings.item;

import com.dlsc.preferencesfx.model.*;
import javafx.collections.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.settings.*;
import thito.nodeflow.internal.settings.*;

public class ListSettingsItem<T> extends AbstractSettingsItem<T> {

    private ObservableList<T> values;

    public ListSettingsItem(String name, I18nItem displayName, ObservableList<T> values, SettingsConverter<T> converter) {
        super(name, displayName, values == null || values.isEmpty() ? null : values.get(0), converter);
        this.values = values;
    }

    @Override
    public ObservableList<T> getPossibleValues() {
        return values;
    }

    @Override
    public Setting createSetting() {
        if (values == null) return Setting.of(getDisplayName().name(), FXCollections.observableArrayList(getValue()), impl_valueProperty());
        return Setting.of(getDisplayName().name(), getPossibleValues(), impl_valueProperty());
    }

}
