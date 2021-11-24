package thito.nodeflow.ui.dialog;

import com.sun.jna.platform.FileUtils;
import thito.nodeflow.language.I18n;
import thito.nodeflow.project.Project;
import thito.nodeflow.task.TaskThread;
import thito.nodeflow.util.Toolkit;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class Dialogs {
    public static void askDeleteFile(List<File> file) {
        FileUtils fileUtils = FileUtils.getInstance();
        if (fileUtils.hasTrash()) {
            askMoveFileToRecycleBin(file);
        } else {
            askDeleteFilePermanently(file);
        }
    }

    public static void askMoveFileToRecycleBin(List<File> file) {
        Dialog.create().title(I18n.$("dialogs.delete-file.title"))
                .message(I18n.$("dialogs.delete-file.message").format(file.stream().map(File::getName).collect(Collectors.joining("\n"))))
                .questionWarning()
                .buttons(
                        DialogButton.create(I18n.$("dialogs.delete-file.buttons.confirm")).defaultButton().mnemonics().execution(() -> {
                            TaskThread.IO().schedule(() -> {
                                for (File f : file) {
                                    Toolkit.moveToRecycleBin(f);
                                }
                            });
                        }),
                        DialogButton.create(I18n.$("dialogs.delete-file.buttons.cancel")).cancelButton().mnemonics())
                .show();
    }

    public static void askDeleteFilePermanently(List<File> file) {
        Dialog.create().title(I18n.$("dialogs.delete-file-permanently.title"))
                .message(I18n.$("dialogs.delete-file.message").format(file.stream().map(File::getName).collect(Collectors.joining("\n"))))
                .questionWarning()
                .buttons(
                        DialogButton.create(I18n.$("dialogs.delete-file-permanently.buttons.delete")).defaultButton().mnemonics().execution(() -> {
                            TaskThread.IO().schedule(() -> {
                                for (File f : file) {
                                    try {
                                        if (f.isDirectory()) {
                                            org.apache.commons.io.FileUtils.deleteDirectory(f);
                                        } else {
                                            org.apache.commons.io.FileUtils.delete(f);
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }),
                        DialogButton.create(I18n.$("dialogs.delete-file-permanently.buttons.cancel")).cancelButton().mnemonics())
                .show();
    }

    public static void askDeleteProject(Project project) {

    }
}
