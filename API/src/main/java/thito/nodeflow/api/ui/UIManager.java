package thito.nodeflow.api.ui;

import thito.nodeflow.api.ui.dialog.DialogManager;
import thito.nodeflow.api.ui.dialog.OpenedDialog;
import thito.nodeflow.api.ui.list.*;

import java.util.List;

public interface UIManager {
    DialogManager getDialogManager();

    Theme getTheme();

    void applyTheme(Theme theme);

    List<OpenedDialog> getOpenedDialogs();

    List<Window> getOpenedWindows();

    Color color(int red, int green, int blue, int alpha);

    IconedList getIconedList();
}
