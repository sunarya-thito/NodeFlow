package thito.nodeflow.internal.ui.editor;

import javafx.application.*;
import javafx.beans.*;
import javafx.collections.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import thito.nodeflow.internal.project.*;
import thito.nodeflow.internal.search.*;
import thito.nodeflow.internal.ui.dashboard.*;
import thito.nodeflow.internal.ui.handler.*;
import thito.nodeflow.internal.binding.*;
import thito.nodeflow.internal.language.*;
import thito.nodeflow.internal.task.*;
import thito.nodeflow.internal.ui.Skin;
import thito.nodeflow.internal.ui.*;

import java.util.*;

public class EditorSkin extends Skin {

    @Component("file-menu")
    Menu fileMenu;

    @Component("file-create")
    Menu fileCreateMenu;

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
            navPanel.setCenter(new EditorFilePanelSkin(this));
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
                        String url = context.getProvider().getIconURL();
                        if (url != null) item.iconProperty().set(new Image(url));
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
