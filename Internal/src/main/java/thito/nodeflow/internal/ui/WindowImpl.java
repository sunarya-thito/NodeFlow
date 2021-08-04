package thito.nodeflow.internal.ui;

import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.css.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import thito.nodeflow.api.*;
import thito.nodeflow.api.bundle.java.*;
import thito.nodeflow.api.event.Event;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.ui.Window;
import thito.nodeflow.api.ui.*;
import thito.nodeflow.api.ui.menu.Menu;
import thito.nodeflow.api.ui.menu.MenuItem;
import thito.nodeflow.api.ui.menu.*;
import thito.nodeflow.api.ui.menu.handler.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.internal.event.window.*;
import thito.nodeflow.internal.locale.*;
import thito.nodeflow.internal.ui.dialog.*;
import thito.nodeflow.internal.ui.menu.*;
import thito.nodeflow.internal.ui.popup.*;
import thito.nodeflow.library.binding.*;
import thito.nodeflow.library.ui.*;
import thito.nodeflow.library.ui.decoration.window.*;
import thito.nodeflow.library.ui.layout.*;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

public abstract class WindowImpl extends WindowBase implements Window {
    private StackPane dialogLayer;
    private LayoutDebugger debugger;
    private Menu menu = new MenuImpl();

    public WindowImpl() {
        // Viewport Hook
        viewportComponent.addListener(this::onViewportChange);
        // UIManager hook
        stage.showingProperty().addListener((obs, old, val) -> {
            if (val) {
                ((UIManagerImpl) NodeFlow.getApplication().getUIManager()).initWindow(this);
            } else {
                ((UIManagerImpl) NodeFlow.getApplication().getUIManager()).disposeWindow(this);
            }
        });
        //
        root.getChildren().addAll(dialogLayer = new StackPane(), debugger = new LayoutDebugger(stage));
        debugger.setNode(content);
        dialogLayer.setPickOnBounds(false);
        stage.getProperties().put(Window.class, this);
        Icon icon = NodeFlow.getApplication().getResourceManager().getIcon("favicon");
        icon.impl_propertyPeer().addListener((obs, old, val) -> {
            if (val != null) {
                stage.getIcons().setAll(val);
            }
        });
        stage.getIcons().setAll(icon.impl_propertyPeer().get());
        scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.F5) {
                if (event.isAltDown()) {
                    reloadTheme();
                } else {
                    reloadCSS();
                }
            } else if (event.getCode() == KeyCode.F1) {
                NotificationPopup.showNotification("# Heading 1\n\n## Heading 2\n\n### Heading 3\n\n#### Heading 4\n\n##### Heading 5\n\nParagraph");
            } else if (event.getCode() == KeyCode.F6) {
                if (debugger.isEnabled()) {
                    debugger.disable();
                } else {
                    debugger.enable();
                }
            } else if (event.getCode() == KeyCode.F7) {
                debugger.setHold(!debugger.isHold());
            } else if (event.getCode() == KeyCode.F8) {
                stage.setAlwaysOnTop(!stage.isAlwaysOnTop());
            } else if (event.getCode() == KeyCode.F9) {
                Dialogs.openCharSelect(this, result -> {});
            } else if (event.getCode() == KeyCode.F10) {
                Dialogs.openEnumSelect(this, ((JavaBundle) NodeFlow.getApplication().getBundleManager().getLoadedBundle("org.spigotmc")).findClass("org.bukkit.Material"), result -> {

                });
            }
        });
        stage.addEventHandler(WindowEvent.WINDOW_SHOWN, event -> {
            if (Event.call(new WindowOpenEventImpl(this)).isConsumed()) {
                event.consume();
            }
        });
        stage.addEventHandler(WindowEvent.WINDOW_HIDDEN, event -> {
            if (Event.call(new WindowCloseEventImpl(this)).isConsumed()) {
                event.consume();
            }
        });
        stage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, event -> {
            if (Event.call(new WindowCloseRequestEventImpl(this)).isConsumed()) {
                event.consume();
            }
        });
        Pseudos.install(root, PseudoClass.getPseudoClass("maximized"), stage.maximizedProperty());
    }

    protected MenuItem requestDefaultApplicationMenu() {
        MenuItem application =
                MenuItem.create(I18n.$("menu-bar-application"), MenuItemType.BUTTON_TYPE,
                        MenuItem.create(I18n.$("menu-bar-application-launcher"), () -> {
                            UIManagerImpl.getInstance().getWindowsManager().getLauncher().show();
                        }, MenuItemType.BUTTON_TYPE),
                        MenuItem.createSeparator(),
                        MenuItem.create(I18n.$("menu-bar-application-preferences"), () -> {
                            UIManagerImpl.getInstance().getWindowsManager().getSettings().show();
                        }, MenuItemType.BUTTON_TYPE),
                        MenuItem.createSeparator(),
                        MenuItem.create(I18n.$("menu-bar-application-quit"), () -> {
                            for (Window window : UIManagerImpl.getInstance().getOpenedWindows()) {
                                window.attemptClose();
                            }
                        }, MenuItemType.BUTTON_TYPE)
                );
        return application;
    }

    protected MenuItem requestDefaultHelpMenu() {
        MenuItem help =
                MenuItem.create(I18n.$("menu-bar-help"), MenuItemType.BUTTON_TYPE,
                        MenuItem.create(I18n.$("menu-bar-help-support"), () -> {
                            try {
                                Desktop.getDesktop().browse(URI.create("https://discord.gg/DquZxC4ZeB"));
                            } catch (IOException e) {
                            }
                        }, MenuItemType.BUTTON_TYPE),
                        MenuItem.create(I18n.$("menu-bar-help-about"), () -> {
                            new AboutDialog(); // instant garbage collect
                        }, MenuItemType.BUTTON_TYPE)
                );
        return help;
    }

    protected MenuItem requestDefaultWindowMenu() {
        MenuItem item = MenuItem.create(I18n.$("menu-bar-window"), MenuItemType.BUTTON_TYPE);
        MenuItem control = MenuItem.create(I18n.$("menu-bar-window-control"), MenuItemType.BUTTON_TYPE);
        control.getChildren().add(MenuItem.create(I18n.$("menu-bar-window-minimize"), () -> {
            stage.setIconified(true);
        }, MenuItemType.BUTTON_TYPE));
        control.getChildren().add(MenuItem.create(I18n.$("menu-bar-window-maximize"), () -> {
            stage.setMaximized(!stage.isMaximized());
        }, MenuItemType.BUTTON_TYPE));
        control.getChildren().add(MenuItem.create(I18n.$("menu-bar-window-close"), () -> {
            stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
        }, MenuItemType.BUTTON_TYPE));
        item.getChildren().add(control);
        item.getChildren().add(MenuItem.createSeparator());
        Map<Window, MenuItem> map = new HashMap<>();
        for (Window window : UIManagerImpl.getInstance().getOpenedWindows()) {
            prepareWindow(item, map, window);
        }
        UIManagerImpl.getInstance().impl_openedWindows().addListener((ListChangeListener<? super Window>) change -> {
            while (change.next()) {
                for (Window window : change.getAddedSubList()) {
                    prepareWindow(item, map, window);
                }
                for (Window window : change.getRemoved()) {
                    item.getChildren().remove(map.get(window));
                }
            }
        });
        return item;
    }

    private void prepareWindow(MenuItem item, Map<Window, MenuItem> map, Window window) {
        RadioButtonMenuItemHandler bx = (RadioButtonMenuItemHandler) map.computeIfAbsent(window, x -> {
            MenuItem menuItem = MenuItem.create(I18nItemImpl.fromBinding(window.impl_getPeer().titleProperty()), () -> {
                window.requestFocus();
            }, MenuItemType.RADIO_BUTTON_TYPE);
            item.getChildren().add(menuItem);
            return menuItem;
        }).getHandler();
        AdvancedBindings.lock(bx.impl_selectedProperty(), window == this);
    }

    @Override
    public Menu getMenu() {
        return menu;
    }

    protected void onViewportChange(Observable observable, UIComponent old, UIComponent value) {
        getDisplayViewport().setComponent(value);
    }

    private void reloadTheme() {
        NodeFlow.getMainLogger().log(Level.INFO, "Reloading theme");
        reloadCSS();
        setViewport(null);
        if (getViewport() == null) {
            initializeViewport();
        }
    }

    private void reloadCSS() {
        NodeFlow.getApplication().getUIManager().applyTheme(NodeFlow.getApplication().getUIManager().getTheme());
    }

    protected void initializeViewport() {
    }

    @Override
    public boolean isShowing() {
        return stage.isShowing();
    }

    private ObjectProperty<UIComponent> viewportComponent = new SimpleObjectProperty<>();

    public void setViewport(UIComponent component) {
        viewportComponent.set(component);
    }

    public UIComponent getViewport() {
        return viewportComponent.get();
    }

    @Override
    public Pane impl_getDialogLayer() {
        return dialogLayer;
    }

    @Override
    public Pane impl_getViewportLayer() {
        return getViewport();
    }

    @Override
    public boolean isFocused() {
        return stage.isFocused();
    }

    @Override
    public void requestFocus() {
        stage.requestFocus();
    }

    @Override
    public void show() {
        if (isShowing()) {
            stage.toFront();
            return;
        }
        if (getViewport() == null) initializeViewport();
        stage.show();
        stage.toFront();
    }

    @Override
    public void hide() {
        if (!isShowing()) return;
        stage.hide();
    }

    @Override
    public void attemptClose() {
        Dialogs.openExitDialog(this);
    }

    @Override
    public void forceClose() {
        stage.close();
        if (NodeFlow.getApplication().getUIManager().getOpenedWindows().isEmpty()) {
            NodeFlow.getApplication().shutdown();
        }
    }

    @Override
    public void setMaximized(boolean maximized) {
        stage.setMaximized(maximized);
    }

    @Override
    public boolean isMaximized() {
        return stage.isMaximized();
    }

    @Override
    public void setMinimized(boolean minimized) {
        stage.setIconified(minimized);
    }

    @Override
    public boolean isMinimized() {
        return stage.isIconified();
    }

    @Override
    public Stage impl_getPeer() {
        return stage;
    }
}
