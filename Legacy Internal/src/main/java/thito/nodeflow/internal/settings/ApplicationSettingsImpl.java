package thito.nodeflow.internal.settings;

import com.dlsc.formsfx.model.util.*;
import com.dlsc.preferencesfx.*;
import com.dlsc.preferencesfx.model.*;
import javafx.collections.*;
import javafx.scene.layout.*;
import thito.nodeflow.api.*;
import thito.nodeflow.api.config.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.resource.*;
import thito.nodeflow.api.settings.*;
import thito.nodeflow.api.ui.*;
import thito.nodeflow.internal.locale.*;
import thito.nodeflow.internal.settings.converter.*;
import thito.nodeflow.internal.settings.item.*;

import java.io.*;
import java.util.*;

public class ApplicationSettingsImpl implements ApplicationSettings {

    public static ApplicationSettingsImpl settings() {
        return (ApplicationSettingsImpl) NodeFlow.getApplication().getSettings();
    }

    private List<SettingsCategory> categories = new ArrayList<>();
    private ObservableList<File> lastSelectedDirectories = FXCollections.observableArrayList();
    private StackPane stackPane = new StackPane();

    public void initializeDefaultSettings() {
        DirectorySettingsItem bundlesDirectorySettings = new DirectorySettingsItem(
                BUNDLES_DIRECTORY, I18n.$("settings-bundles-directory"),
                new File("Bundles").getAbsoluteFile(), lastSelectedDirectories
        );
        DirectorySettingsItem workspaceDirectorySettings = new DirectorySettingsItem(
                WORKSPACE_DIRECTORY, I18n.$("settings-workspace-directory"),
                new File("Workspace").getAbsoluteFile(), lastSelectedDirectories
        );
        lastSelectedDirectories.addAll(bundlesDirectorySettings.getDefaultValue(), workspaceDirectorySettings.getDefaultValue());
        BooleanSettingsItem askBeforeExitSettings = new BooleanSettingsItem(ASK_BEFORE_EXIT, I18n.$("settings-ask-before-exit"), true);
//        BooleanSettingsItem showIntro = new BooleanSettingsItem(SHOW_INTRO, I18n.$("settings-show-intro"), true);
        ObservableList<Language> languages = FXCollections.observableArrayList();
        for (String loc : Locale.getISOLanguages()) {
            Language language = new Language(loc);
            if (language.isAvailable()) {
                languages.add(language);
            }
        }
        Collections.sort(languages);
        ListSettingsItem<Language> locale = new ListSettingsItem<>(LANGUAGE, I18n.$("settings-language"), languages, LanguageConverter.LANGUAGE_CONVERTER);
        locale.setValue(new Language("en"));
        locale.impl_valueProperty().addListener((obs, old, val) -> {
            val.load();
            // to refresh
            if (fx != null) {
                fx.i18n(new TranslationService() {
                    @Override
                    public String translate(String s) {
                        return "";
                    }
                });
                fx.i18n((TranslationService) NodeFlow.getApplication().getLocaleManager());
            }
        });
        ObservableList<Theme> themes = FXCollections.observableArrayList();
        for (Resource theme : ((ResourceDirectory) NodeFlow.getApplication().getResourceManager().getResource("themes")).getChildren()) {
            themes.add(NodeFlow.getApplication().getResourceManager().getTheme(theme.getName()));
        }
        ListSettingsItem<Theme> themeList = new ListSettingsItem<>(THEME, I18n.$("settings-theme"), themes, new SettingsConverter<Theme>() {
            @Override
            public Theme deserialize(SettingsItem<Theme> requester, String name, Section section) {
                return NodeFlow.getApplication().getResourceManager().getTheme(section.getString(themes));
            }

            @Override
            public void serialize(SettingsItem<Theme> requester, String name, Section section, Theme value) {
                section.set(value.getName(), name);
            }
        });
        themeList.setValue(NodeFlow.getApplication().getUIManager().getTheme());
        getCategories().addAll(Arrays.asList(
                createCategory(I18n.$("settings-category-general"),
                            createGroup(I18n.$("settings-group-application"),
                                        askBeforeExitSettings,
                                        locale,
                                        themeList
//                                        showIntro
                                    ),
                            createGroup(I18n.$("settings-group-workspace"), workspaceDirectorySettings)
                        ),
                createCategory(I18n.$("settings-category-addon"),
                        createGroup(I18n.$("settings-category-general"),
                                bundlesDirectorySettings))
        ));
    }

    private PreferencesFx fx;
    public void updatePreferencesFX() {
        stackPane.getChildren().setAll(createPreferencesFX().getView());
    }

    @Override
    public SettingsGroup createGroup(String name, I18nItem displayName, SettingsItem<?>... items) {
        SettingsGroup group = new SettingsGroupImpl(name, displayName);
        group.getItems().addAll(Arrays.asList(items));
        return group;
    }

    @Override
    public SettingsCategory createCategory(String name, I18nItem displayName, SettingsGroup... groups) {
        SettingsCategory category = new SettingsCategoryImpl(name, displayName);
        category.getGroups().addAll(Arrays.asList(groups));
        return category;
    }

    public StackPane getView() {
        return stackPane;
    }

    public PreferencesFx createPreferencesFX() {
        fx = PreferencesFx.of(ApplicationSettingsImpl.class, getCategories().stream().map(category ->
                ((SettingsCategoryImpl) category).toCategory())
                .toArray(Category[]::new))
                .i18n((TranslationService) NodeFlow.getApplication().getLocaleManager());
        return fx;
    }

    public PreferencesFx getPreferencesFX() {
        return fx;
    }

    public void load(ResourceFile settingsYml) {
        MapSection section = (MapSection) Section.loadYaml(settingsYml);
        if (section != null) {
            for (SettingsItem item : getSettingsItems()) {
                Object value = item.getConverter().deserialize(item, item.name(), section);
                item.setValue(value);
            }
        }
    }

    private List<SettingsItem<?>> getSettingsItems() {
        ArrayList<SettingsItem<?>> items = new ArrayList<>();
        collect(items, getCategories());
        return items;
    }

    private void collect(List<SettingsItem<?>> items, SettingsCategory category) {
        for (int i = 0; i < category.getGroups().size(); i++) {
            collect(items, category.getGroups().get(i));
        }
        collect(items, category.getChildren());
    }

    private void collect(List<SettingsItem<?>> items, SettingsGroup category) {
        for (int i = 0; i < category.getItems().size(); i++) {
            items.add(category.getItems().get(i));
        }
    }

    private void collect(List<SettingsItem<?>> items, List<SettingsCategory> categories) {
        for (int i = 0; i < categories.size(); i++) {
            collect(items, categories.get(i));
        }
    }

    public void save(WritableResourceFile settingsYml) {
        MapSection section = Section.newMap();
        for (SettingsItem item : getSettingsItems()) {
            item.getConverter().serialize(item, item.name(), section, item.getValue());
        }
        Section.saveYaml(section, settingsYml);
    }

    @Override
    public List<SettingsCategory> getCategories() {
        return categories;
    }
}
