package thito.nodeflow.internal.settings.item;

import com.dlsc.formsfx.model.validators.*;
import com.dlsc.preferencesfx.model.*;
import javafx.collections.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.internal.settings.*;
import thito.nodeflow.internal.settings.converter.*;

import java.io.*;

public class DirectorySettingsItem extends AbstractSettingsItem<File> {
    private ObservableList<File> suggestedValues;
    public DirectorySettingsItem(String name, I18nItem displayName, File defaultValue, ObservableList<File> suggestedValues) {
        super(name, displayName, defaultValue, FileConverter.FILE_CONVERTER);
        this.suggestedValues = suggestedValues;
    }

    @Override
    public ObservableList<File> getPossibleValues() {
        return suggestedValues;
    }

    private File getLatestDirectory() {
        if (!suggestedValues.isEmpty()) {
            File suggested = suggestedValues.get(suggestedValues.size() - 1);
            return rootExists(suggested);
        } else return rootExists(getValue());
    }

    private File rootExists(File file) {
        for (;;) {
            if (file == null || file.isDirectory()) return file;
            file = file.getParentFile();
        }
    }

    @Override
    public Setting createSetting() {
        return Setting.of(getDisplayName().name(), impl_valueProperty(), getLatestDirectory(), true).validate(CustomValidator.forPredicate(value -> new File(value.toString()).isDirectory(), I18n.$("directory-does-not-exist").name()));
    }

}
