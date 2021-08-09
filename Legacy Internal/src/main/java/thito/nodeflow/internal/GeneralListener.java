package thito.nodeflow.internal;

import com.sun.javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.*;
import thito.nodeflow.api.*;
import thito.nodeflow.api.event.*;
import thito.nodeflow.api.event.window.*;
import thito.nodeflow.api.settings.*;
import thito.nodeflow.api.task.*;
import thito.nodeflow.api.ui.Window;
import thito.nodeflow.api.ui.*;
import thito.nodeflow.internal.ui.*;
import thito.nodeflow.internal.ui.dialog.*;
import thito.nodeflow.internal.ui.splash.*;
import thito.nodeflow.library.ui.*;
import thito.nodeflow.library.ui.decoration.popup.*;

public class GeneralListener extends FXListener implements Listener {

    @EventBus
    public void onWindowCloseRequest(WindowCloseRequestEvent event) {
        Window window = event.getWindow();
        if (window instanceof ConfirmationClose) {
            if (NodeFlow.getApplication().getSettings().<Boolean>getValue(ApplicationSettings.ASK_BEFORE_EXIT) && ((ConfirmationClose) window).askFirstBeforeClosing()) {
                Dialogs.openExitDialog(window);
            } else {
                if (!(window instanceof LauncherWindow) && UIManagerImpl.getInstance().getOpenedWindows().size() <= 1) {
                    UIManagerImpl.getInstance().getWindowsManager().getLauncher().show();
                }
                window.forceClose();
            }
            event.consume();
        }
    }

    @EventBus
    public void onWindowClosed(WindowCloseEvent event) {
        if (event.getWindow() instanceof LauncherWindow) {
            if (NodeFlow.getApplication().getProjectManager().getLoadedProjects().length <= 0) {
                NodeFlow.getApplication().shutdown();
            }
        }
    }

    @Override
    public void onNodeAdded(Node node) {
//        if (!(node instanceof Parent) && !(node instanceof Text)) {
//            node.setCache(true);
//            node.setCacheHint(CacheHint.SPEED);
//            if (node instanceof Region) {
//                ((Region) node).setCacheShape(true);
//            }
//        }
        Pseudos.install(node, Pseudos.VISIBLE, node.opacityProperty().greaterThan(0));
        if (node instanceof TextField) {
            ((TextField) node).textProperty();
        }
        if (node instanceof Pane) {
            node.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
                if (!node.isFocused()) {
                    node.requestFocus();
                }
            });
        }
        if (node instanceof TickableNode) {
            Ticker.register((TickableNode) node);
        }
    }

    @Override
    public void onNodeRemoved(Node node) {
        if (node instanceof TickableNode) {
            Ticker.unregister((TickableNode) node);
        }
    }

    @Override
    public void onStageAdded(javafx.stage.Window stage) {
        if (!(stage instanceof ExceptionalStage) && stage instanceof Stage && ((Stage) stage).getOwner() == null && stage != PopupBase.getInvisibleParent()) {
            stage.getProperties().put(StaticMenu.class, new StaticMenu((Stage) stage));
        }
//        stage.focusedProperty().addListener((obs, old, val) -> {
//            if (val) {
//                currentFocus.set(stage);
//            } else {
//                Platform.runLater(() -> {
//                    if (currentFocus.get() == stage) {
//                        currentFocus.set(null);
//                    }
//                });
//            }
//        });
    }

    @Override
    public void onStageRemoved(javafx.stage.Window stage) {
        if (!(stage instanceof SplashScreen.SplashScreenStage)) {
            StaticMenu menu = (StaticMenu) stage.getProperties().get(StaticMenu.class);
            if (menu != null) {
                menu.destroy();
            }
        }
    }
}
