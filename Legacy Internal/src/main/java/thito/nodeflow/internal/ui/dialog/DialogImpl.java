package thito.nodeflow.internal.ui.dialog;

import thito.nodeflow.api.ui.*;
import thito.nodeflow.api.ui.dialog.*;
import thito.nodeflow.api.ui.dialog.button.*;
import thito.nodeflow.api.ui.dialog.content.*;

import java.util.*;
import java.util.concurrent.*;

public class DialogImpl implements Dialog {
    private Set<OpenedDialog> openedDialogs = ConcurrentHashMap.newKeySet();
    private int options;
    private List<DialogButton> buttons = new ArrayList<>();
    private DialogContent content;

    public DialogImpl(int options, DialogContent content, DialogButton... buttons) {
        this.options = options;
        this.content = content;
        for (DialogButton button : buttons) {
            getButtons().add(button);
        }
    }

    @Override
    public DialogContent getContent() {
        return content;
    }

    @Override
    public List<DialogButton> getButtons() {
        return buttons;
    }

    public void setDialogOptions(int options) {
        this.options = options;
    }

    @Override
    public int getDialogOptions() {
        return options;
    }

    @Override
    public Set<OpenedDialog> getOpenedDialogs() {
        return openedDialogs;
    }

    @Override
    public void close() {
        for (OpenedDialog dialog : openedDialogs) {
            dialog.close(null);
        }
    }

    @Override
    public OpenedDialog open(Window owner) {
        OpenedDialog dialog = new OpenedDialogImpl(this, owner);
        openedDialogs.add(dialog);
        return dialog;
    }
}
