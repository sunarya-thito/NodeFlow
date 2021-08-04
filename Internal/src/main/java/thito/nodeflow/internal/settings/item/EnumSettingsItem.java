package thito.nodeflow.internal.settings.item;

import com.dlsc.preferencesfx.model.*;
import javafx.collections.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.internal.settings.*;
import thito.nodeflow.internal.settings.converter.*;

public class EnumSettingsItem<T extends Enum<T>> extends AbstractSettingsItem<T> {
    private static <T extends Enum<T>> T defaultValue(Class<T> type) {
        return type.getEnumConstants()[0];
    }
    private T[] enums;
    public EnumSettingsItem(String name, I18nItem displayName, Class<T> type) {
        super(name, displayName, defaultValue(type), new EnumConverter(type));
        enums = type.getEnumConstants();
    }

    @Override
    public ObservableList<T> getPossibleValues() {
        return FXCollections.observableArrayList(enums);
    }

    @Override
    public Setting createSetting() {
        return Setting.of(getDisplayName().name(), getPossibleValues(), impl_valueProperty());
    }
}
