package thito.nodeflow.library;

import thito.nodeflow.api.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.task.*;
import thito.nodeflow.api.ui.*;
import thito.nodeflow.api.ui.dialog.*;
import thito.nodeflow.api.ui.dialog.button.*;
import thito.nodeflow.api.ui.dialog.content.*;
import thito.nodeflow.internal.ui.dialog.*;

import java.io.*;
import java.nio.file.*;

public class FileOperationChain {

    private Window freeze;
    private FileTask root;
    private boolean suppressWarnings;
    private boolean skip;

    public FileOperationChain(Window freeze) {
        this.freeze = freeze;
    }

    public int warnCount(FileTask current) {
        int count = 0;
        while (current != null) {
            if (current.warnExistence()) {
                count++;
            }
            current = current.next;
        }
        return count;
    }

    public void execute() {
        if (root != null) {
            root.execute();
        }
    }

    public boolean contains(File source) {
        FileTask current = root;
        while (current != null) {
            if (current.source.getPath().equals(source.getAbsolutePath())) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    public void copyFile(File source, File target) {
        File[] listFiles = source.listFiles();
        if (listFiles != null) {
            for (File file : listFiles) {
                copyFile(file, new File(target, source.getName()));
            }
            return;
        }
        if (contains(source)) return;
        appendTask(new FileTask(FileTask.COPY, source, target));
    }

    public void moveFile(File source, File target) {
        File[] listFiles = source.listFiles();
        if (listFiles != null) {
            for (File file : listFiles) {
                moveFile(file, new File(target, source.getName()));
            }
            return;
        }
        if (contains(source)) return;
        appendTask(new FileTask(FileTask.MOVE, source, target));
    }

    private void appendTask(FileTask task) {
        if (root == null) {
            root = task;
        } else {
            FileTask current = root;
            while (current.next != null) current = current.next;
            current.next = task;
        }
    }

    public class FileTask {
        public static final int MOVE = 0;
        public static final int COPY = 1;
        private int operation;
        private File source;
        private File target;
        private FileTask next;
        private boolean suppress;
        private String duplicate = null;

        public FileTask(int operation, File source, File target) {
            this.operation = operation;
            this.source = source;
            this.target = target;
        }

        public boolean warnExistence() {
            return new File(target, getSourceName()).exists();
        }

        private String getSourceName() {
            return duplicate == null ? source.getName() : duplicate + source.getName();
        }

        public void executeNext() {
            if (next != null) next.execute();
        }

        public void execute() {
            if (new File(target, getSourceName()).getAbsolutePath().equalsIgnoreCase(source.getAbsolutePath())) {
                if (operation == COPY) {
                    String additional = "Copy of ";
                    while (new File(target, additional + source.getName()).exists()) {
                        additional += "Copy of ";
                    }
                    duplicate = additional;
                    execute();
                } else {
                    executeNext();
                }
                return;
            }
            if (warnExistence() && !suppressWarnings && !suppress) {
                if (skip) {
                    executeNext();
                    return;
                }
                Task.runOnForeground("warn-file-exists", () -> {
                    DialogManager dialogManager = NodeFlow.getApplication().getUIManager().getDialogManager();
                    DialogContent dialogContent = dialogManager.createMessageContent(
                            Dialog.Type.QUESTION,
                            Dialog.Level.WARN,
                            I18n.$("file-replace-ask-title"),
                            Pos.LEFT,
                            I18n.$("file-replace-ask-message").format(source.getName()),
                            Pos.LEFT);
                    int count = warnCount(this);
                    TextDialogButton dialogButton1 = dialogManager.createTextButton(1, DialogButton.DEFAULT_BUTTON, I18n.$("file-replace-ask-replace"), null, clickAction -> {
                        suppress = true;
                        Task.runOnBackground("file-task", this::execute);
                        clickAction.close();
                    });
                    TextDialogButton dialogButton2 = dialogManager.createTextButton(2, 0, I18n.$("file-replace-ask-skip"), null, clickAction -> {
                        Task.runOnBackground("file-task", this::executeNext);
                        clickAction.close();
                    });
                    if (count > 1) {
                        TextDialogButton dialogButton3 = dialogManager.createTextButton(2, 0, I18n.$("file-replace-ask-replace-all").format(count), null, clickAction -> {
                            suppressWarnings = true;
                            Task.runOnBackground("file-task", this::execute);
                            clickAction.close();
                        });
                        TextDialogButton dialogButton4 = dialogManager.createTextButton(2, 0, I18n.$("file-replace-ask-skip-all").format(count), null, clickAction -> {
                            skip = true;
                            Task.runOnBackground("file-task", this::executeNext);
                            clickAction.close();
                        });
                        Dialog ask = dialogManager.createDialog(dialogContent, Dialog.OVERLAY_CLOSE,
                                dialogButton2, dialogButton1, dialogButton4, dialogButton3);
                        ask.open(freeze);
                    } else {
                        Dialog ask = dialogManager.createDialog(dialogContent, Dialog.OVERLAY_CLOSE,
                                dialogButton2, dialogButton1);
                        ask.open(freeze);
                    }
                });
            } else {
                try {
                    if (!proceed()) {
                        Task.runOnForeground("warn", () -> {
                            if (operation == COPY) {
                                Dialogs.inform(freeze, I18n.$("file-replace-failed-title"), I18n.$("file-replace-failed-copy").format(source.toString()), Dialog.Type.INFO, Dialog.Level.DANGER, () -> {
                                    Task.runOnBackground("file-task", this::executeNext);
                                });
                            } else if (operation == MOVE) {
                                Dialogs.inform(freeze, I18n.$("file-replace-failed-title"), I18n.$("file-replace-failed-move").format(source.toString()), Dialog.Type.INFO, Dialog.Level.DANGER, () -> {
                                    Task.runOnBackground("file-task", this::executeNext);
                                });
                            }
                        });
                    } else {
                        executeNext();
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                    Task.runOnForeground("warn", () -> {
                        if (operation == COPY) {
                            Dialogs.inform(freeze, I18n.$("file-replace-failed-title"), I18n.$("file-replace-failed-copy").format(source.toString()), Dialog.Type.INFO, Dialog.Level.DANGER, () -> {
                                Task.runOnBackground("file-task", this::executeNext);
                            });
                        } else if (operation == MOVE) {
                            Dialogs.inform(freeze, I18n.$("file-replace-failed-title"), I18n.$("file-replace-failed-move").format(source.toString()), Dialog.Type.INFO, Dialog.Level.DANGER, () -> {
                                Task.runOnBackground("file-task", this::executeNext);
                            });
                        }
                    });
                }
            }
        }

        private boolean proceed() throws IOException {
            if (operation == COPY) {
                if (source.isDirectory()) {
                    throw new IOException("can't copy directory");
                } else {
                    target.mkdirs();
                    Files.copy(source.toPath(), new File(target, getSourceName()).toPath());
                }
            } else if (operation == MOVE) {
                if (source.isDirectory()) {
                    throw new IOException("can't move directory");
                } else {
                    new File(target, source.getName()).delete();
                    target.mkdirs();
                    return source.renameTo(new File(target, getSourceName()));
                }
            }
            return true;
        }

    }
}
