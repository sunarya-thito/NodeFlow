package thito.nodeflow.internal.ui.editor;

import javafx.application.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import thito.nodeflow.internal.settings.*;
import thito.nodeflow.internal.settings.general.*;
import thito.nodeflow.internal.ui.dashboard.*;
import thito.nodeflow.library.language.*;
import thito.nodeflow.library.ui.Skin;
import thito.nodeflow.library.ui.*;
import thito.nodeflow.library.ui.handler.*;

import java.util.*;

public class EditorSkin extends Skin {

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

    private SearchPopup searchPopup;
    private int menuIndex;
    private int navPanelIndex;
    private double navPanelDividerPosition;

    @Override
    protected void initializeSkin() {
        super.initializeSkin();
        registerActionHandler("window.openSettings", ActionEvent.ACTION, event -> {
        });
        registerActionHandler("window.openDashboard", ActionEvent.ACTION, event -> DashboardWindow.getWindow().show());
        registerActionHandler("editor.navigation.file", ActionEvent.ACTION, event -> {
            navPanel.setCenter(new EditorFilePanelSkin());
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
        for (int i = 0; i < 30; i++) {
            SearchResultItem item = new SearchResultItem(I18n.direct("Title test"), I18n.direct("from NodeFlow wiki"));
            item.iconProperty().set(new Image(new Random().nextBoolean() ? "rsrc:Themes/Dark/Icons/GlobeIcon.png" : "rsrc:Themes/Dark/Icons/FileIcon.png"));
            searchPopup.getSearchResultItems().add(item);
        }
    }

    public int getMenuIndex() {
        return menuIndex;
    }

    public MenuBar getMenuBar() {
        return menuBar;
    }
}
