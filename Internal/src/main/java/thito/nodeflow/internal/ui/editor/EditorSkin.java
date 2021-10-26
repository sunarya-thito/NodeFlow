package thito.nodeflow.internal.ui.editor;

import javafx.application.*;
import javafx.beans.*;
import javafx.beans.binding.*;
import javafx.beans.value.*;
import javafx.collections.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import thito.nodeflow.internal.binding.*;
import thito.nodeflow.internal.language.*;
import thito.nodeflow.internal.plugin.PluginManager;
import thito.nodeflow.internal.project.*;
import thito.nodeflow.internal.project.module.FileModule;
import thito.nodeflow.internal.resource.Resource;
import thito.nodeflow.internal.resource.ResourceType;
import thito.nodeflow.internal.search.*;
import thito.nodeflow.internal.task.*;
import thito.nodeflow.internal.ui.Skin;
import thito.nodeflow.internal.ui.*;
import thito.nodeflow.internal.ui.dashboard.*;
import thito.nodeflow.internal.ui.form.CreateFileForm;
import thito.nodeflow.internal.ui.form.Validator;
import thito.nodeflow.internal.ui.handler.*;

public class EditorSkin extends Skin {

    @Component("file-menu")
    Menu fileMenu;

    @Component("maximize-button")
    Button maximizeButton;

    @Component("editor-root")
    BorderPane root;

    @Component("window-caption")
    BorderPane caption;

    @Component("module-menu")
    Menu moduleMenu;

    @Component("menu-bar")
    MenuBar menuBar;

    @Component("search-bar")
    HBox searchBar;

    @Component("search-field")
    TextField searchField;

    @Component("main-viewport")
    SplitPane mainViewport;

    @Component("nav-panel")
    BorderPane navPanel;

    @Component("file-tabs")
    TabPane fileTabs;

    @Component("file-create")
    Menu newFile;
    EditorFilePanelSkin filePanel = new EditorFilePanelSkin(this);

    private SearchPopup searchPopup;
    private int menuIndex;
    private int navPanelIndex;
    private double navPanelDividerPosition;
    private EditorWindow editorWindow;

    public EditorSkin(EditorWindow editorWindow) {
        this.editorWindow = editorWindow;
    }

    @Override
    protected void initializeSkin() {
        super.initializeSkin();
        registerActionHandler("window.openSettings", ActionEvent.ACTION, event -> {
//            SettingsWindow.open();
            // TODO open settings
        });
        registerActionHandler("window.openDashboard", ActionEvent.ACTION, event -> DashboardWindow.getWindow().show());

        registerActionHandler("editor.navigation.file", ActionEvent.ACTION, event -> {
            navPanel.setCenter(filePanel);
        });
        ToggleGroup navigation = ToggleButtonSkinHandler.getGroup("navigation-editor");
        navigation.selectedToggleProperty().addListener((obs, old, val) -> {
            if (val != null) {
                if (!mainViewport.getItems().contains(navPanel)) {
                    mainViewport.getItems().add(navPanelIndex, navPanel);
                    mainViewport.setDividerPosition(navPanelIndex, navPanelDividerPosition);
                }
            } else {
                navPanelDividerPosition = mainViewport.getDividerPositions()[navPanelIndex];
                mainViewport.getItems().remove(navPanelIndex);
            }
        });
    }

    @Override
    protected void onLayoutLoaded() {
        for (FileModule module : PluginManager.getPluginManager().getModuleList()) {
            MenuItem menuItem = new MenuItem();
            menuItem.setGraphic(new ImageView(module.getIconURL(ThemeManager.getInstance().getTheme())));
            menuItem.textProperty().bind(module.getDisplayName());
            menuItem.addEventHandler(ActionEvent.ACTION, event -> {
                TreeItem<Resource> selected = filePanel.getExplorerView().getSelectionModel().getSelectedItem();
                Resource root = selected != null ? selected.getValue() : null;
                if (root == null) root = getEditorWindow().getEditor().projectProperty().get().getSourceFolder();
                showCreateFileForm(module, root);
            });
            newFile.getItems().add(menuItem);
        }
        searchPopup = new SearchPopup();
        Menu parent = moduleMenu.getParentMenu();
        if (parent != null) {
            menuIndex = moduleMenu.getParentMenu().getItems().indexOf(moduleMenu);
            moduleMenu.getParentMenu().getItems().remove(menuIndex);
        } else {
            menuIndex = menuBar.getMenus().indexOf(moduleMenu);
            menuBar.getMenus().remove(menuIndex);
        }

        navPanelIndex = mainViewport.getItems().indexOf(navPanel);
        navPanelDividerPosition = mainViewport.getDividerPositions()[navPanelIndex];
        mainViewport.getItems().remove(navPanelIndex);

        searchField.textProperty().addListener((obs, old, val) -> {
            updateSearch(val);
        });

        searchBar.layoutBoundsProperty().addListener(obs -> updateSearchPopupPosition());
        getScene().getWindow().xProperty().addListener(obs -> updateSearchPopupPosition());
        getScene().getWindow().yProperty().addListener(obs -> updateSearchPopupPosition());
        getScene().getWindow().widthProperty().addListener(obs -> updateSearchPopupPosition());
        getScene().getWindow().heightProperty().addListener(obs -> updateSearchPopupPosition());
        updateSearchPopupPosition();

        editorWindow.getEditor().getOpenedTabs().addListener((ListChangeListener<EditorTab>) change -> {
            while (change.next()) {
                if (change.wasRemoved()) {
                    for (EditorTab t : change.getRemoved()) {
                        fileTabs.getTabs().remove(t.getTab());
                    }
                }
                if (change.wasAdded()) {
                    for (EditorTab t : change.getAddedSubList()) {
                        fileTabs.getTabs().add(t.getTab());
                    }
                }
            }
        });
        fileTabs.getTabs().addListener((InvalidationListener) obs -> {
            for (Tab tab : fileTabs.getTabs()) {
                FileTab fileTab = (FileTab) tab.getProperties().get(FileTab.class);
                if (fileTab != null) {
                    fileTab.updateName();
                }
            }
        });

        editorWindow.getEditor().projectProperty().addListener((obs, old, val) -> {
            if (val == null) {
                ThreadBinding.bind(fileMenu.textProperty(), I18n.$("editor.file"), TaskThread.UI());
            } else {
                ThreadBinding.bind(fileMenu.textProperty(), val.getProperties().nameProperty(), TaskThread.UI());
            }
        });

        Project project = editorWindow.getEditor().projectProperty().get();
        if (project == null) {
            ThreadBinding.bind(fileMenu.textProperty(), I18n.$("editor.file"), TaskThread.UI());
        } else {
            ThreadBinding.bind(fileMenu.textProperty(), project.getProperties().nameProperty(), TaskThread.UI());
        }

        searchField.focusedProperty().addListener((obs, old, val) -> {
            if (val) {
                if (!searchPopup.isShowing()) {
                    searchPopup.show(this, 0, 0);
                    updateSearchPopupPosition();
                }
            } else {
                searchPopup.hide();
            }
        });
    }

    static void showCreateFileForm(FileModule module, Resource root) {
        if (root.getType() == ResourceType.FILE) {
            root = root.getParent();
        }
        CreateFileForm form = new CreateFileForm();
        Validator<Resource> resourceValidator = module.getFileValidator();
        Resource finalRoot = root;
        form.name.validate(
                Validator.combine(
                        Validator.combine(
                                Validator.mustNotEmpty(),
                                Validator.validFilename()),
                        value -> resourceValidator.validate(finalRoot.getChild(value))));
        form.type.validate(Validator.mustNotEmpty());
        form.type.set(module);
        FormDialog.create(I18n.$("dialogs.new-file.title"), form).show(result -> {
            if (result != null) {
                Resource newFile = finalRoot.getChild(result.name.get());
                result.type.get().createFile(newFile);
            }
        });
    }

    public EditorWindow getEditorWindow() {
        return editorWindow;
    }

    void updateSearchPopupPosition() {
        Platform.runLater(() -> {
            Bounds screen = searchBar.localToScreen(searchBar.getLayoutBounds());
            searchPopup.setX(screen.getMinX() - 10);
            searchPopup.setY(screen.getMaxY() + 10);
            searchPopup.getSkin().setMinWidth(screen.getWidth());
            searchPopup.getSkin().setMaxWidth(screen.getWidth());
        });
    }

    private void updateSearch(String string) {
        updateSearchPopupPosition();
        searchPopup.getSearchResultItems().clear();
        Editor editor = editorWindow.getEditor();
        SearchQuery query = new SearchQuery(string);
        SearchThread.submit(() -> {
            for (SearchableContentContext context : editor.getSearchableContentContexts()) {
                for (SearchableContent content : context.getSearchableContentList()) {
                    SearchSession searchSession = content.search(query);
                    if (searchSession == null) continue;
                    for (SearchResult result : searchSession.getResults()) {
                        SearchResultItem item = new SearchResultItem(result.getTitle(), I18n.$("search-source").format(searchSession.getName()), result);
                        ObservableValue<String> iconUrl = context.getProvider().iconURLProperty();
                        if (iconUrl != null) item.iconProperty().bind(Bindings.createObjectBinding(() -> new Image(iconUrl.getValue()), iconUrl));
                        TaskThread.UI().schedule(() -> {
                            searchPopup.getSearchResultItems().add(item);
                        });
                    }
                }
            }
        });
    }

    public int getMenuIndex() {
        return menuIndex;
    }

    public MenuBar getMenuBar() {
        return menuBar;
    }
}
