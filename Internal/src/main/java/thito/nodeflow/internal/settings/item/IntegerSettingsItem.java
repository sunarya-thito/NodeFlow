package thito.nodeflow.internal.settings.item;

import com.dlsc.preferencesfx.model.*;
import javafx.collections.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.settings.*;
import thito.nodeflow.internal.settings.*;
import thito.nodeflow.library.binding.*;

public class IntegerSettingsItem extends AbstractSettingsItem<Number> {
    public IntegerSettingsItem(String name, I18nItem displayName, SettingsConverter<Number> converter) {
        super(name, displayName, 0, converter);
    }

    @Override
    public ObservableList<Number> getPossibleValues() {
        return null;
    }

    @Override
    public Setting createSetting() {
        return Setting.of(getDisplayName().name(), ConvertProperty.convertToInteger(impl_valueProperty()));
    }
}
