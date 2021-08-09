package thito.nodeflow.internal.settings.item;

import com.dlsc.preferencesfx.model.*;
import javafx.beans.property.*;
import javafx.collections.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.internal.settings.*;
import thito.nodeflow.internal.settings.converter.*;
import thito.nodeflow.library.binding.*;

public class BooleanSettingsItem extends AbstractSettingsItem<Boolean> {
    public BooleanSettingsItem(String name, I18nItem displayName, boolean defaultValue) {
        super(name, displayName, defaultValue, (ObjectConverter<Boolean>) (requester, name1, section) -> section.getBoolean(name1));
    }

    @Override
    public ObservableList<Boolean> getPossibleValues() {
        return null;
    }

    @Override
    public Setting createSetting() {
        Setting setting = Setting.of(getDisplayName().name(), ConvertProperty.convertBoolean(impl_valueProperty()));
        return setting;
    }
}
