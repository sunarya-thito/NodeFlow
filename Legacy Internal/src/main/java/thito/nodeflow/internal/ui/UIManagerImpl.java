package thito.nodeflow.internal.ui;

import com.sun.javafx.stage.*;
import javafx.collections.*;
import javafx.scene.*;
import javafx.stage.*;
import thito.nodeflow.api.*;
import thito.nodeflow.api.ui.Window;
import thito.nodeflow.api.ui.*;
import thito.nodeflow.api.ui.dialog.*;
import thito.nodeflow.api.ui.list.*;
import thito.nodeflow.internal.ui.dialog.*;
import thito.nodeflow.internal.ui.list.*;

import java.util.*;

public class UIManagerImpl implements UIManager {

    public static UIManagerImpl getInstance() {
        return (UIManagerImpl) NodeFlow.getApplication().getUIManager();
    }
    private final DialogManager dialogManager = new DialogManagerImpl();
    private List<OpenedDialog> openedDialogs = new ArrayList<>();
    private ObservableList<Window> openedWindows = FXCollections.observableArrayList();
    private WindowsManager windowsManager = new WindowsManager();
    private Theme theme;
    private IconedList iconedList = new IconedListImpl();

    public UIManagerImpl() {
        StageHelper.getStages().addListener((ListChangeListener<? super Stage>) c -> {
            while (c.next()) {
                if (theme != null) {
                    for (Stage added : c.getAddedSubList()) {
                        Scene scene = added.getScene();
                        if (scene != null) {
                            scene.getStylesheets().clear();
                            scene.getStylesheets().setAll(theme.getCSSPaths((Window) added.getProperties().get(Window.class)));
                        }
                    }
                }
            }
        });
    }

    @Override
    public IconedList getIconedList() {
        return iconedList;
    }

    public WindowsManager getWindowsManager() {
        return windowsManager;
    }

    @Override
    public DialogManager getDialogManager() {
        return dialogManager;
    }

    @Override
    public Theme getTheme() {
        return theme;
    }

    @Override
    public void applyTheme(Theme theme) {
        this.theme = theme;
        for (Stage stage : StageHelper.getStages()) {
            Window base = (Window) stage.getProperties().get(Window.class);
            Scene scene = stage.getScene();
            if (scene != null) {
                stage.getScene().getStylesheets().clear();
                stage.getScene().getStylesheets().setAll(theme.getCSSPaths(base));
            }
        }
    }

    public void initDialog(OpenedDialog dialog) {
        if (!openedDialogs.contains(dialog)) {
            openedDialogs.add(dialog);
        }
    }

    public void disposeDialog(OpenedDialog dialog) {
        openedDialogs.remove(dialog);
    }

    public void initWindow(Window window) {
        if (!openedWindows.contains(window)) {
            openedWindows.add(window);
        }
    }

    @Deprecated
    public ObservableList<Window> impl_openedWindows() {
        return openedWindows;
    }

    public void disposeWindow(Window window) {
        openedWindows.remove(window);
    }

    @Override
    public List<OpenedDialog> getOpenedDialogs() {
        return Collections.unmodifiableList(openedDialogs);
    }

    @Override
    public List<Window> getOpenedWindows() {
        return Collections.unmodifiableList(openedWindows);
    }

    @Override
    public Color color(int red, int green, int blue, int alpha) {
        return new ColorImpl(red, green, blue, alpha);
    }
}
