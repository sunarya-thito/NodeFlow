package thito.nodeflow.internal.ui.dialog;

import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.stage.*;
import thito.nodeflow.api.*;
import thito.nodeflow.api.editor.*;
import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.project.*;
import thito.nodeflow.api.resource.*;
import thito.nodeflow.api.settings.*;
import thito.nodeflow.api.task.*;
import thito.nodeflow.api.ui.Pos;
import thito.nodeflow.api.ui.Window;
import thito.nodeflow.api.ui.dialog.Dialog;
import thito.nodeflow.api.ui.dialog.*;
import thito.nodeflow.api.ui.dialog.button.*;
import thito.nodeflow.api.ui.dialog.content.*;
import thito.nodeflow.internal.editor.*;
import thito.nodeflow.internal.node.*;
import thito.nodeflow.internal.project.*;
import thito.nodeflow.internal.resource.*;
import thito.nodeflow.internal.ui.*;
import thito.nodeflow.internal.ui.dialog.content.*;
import thito.nodeflow.internal.ui.editor.*;
import thito.nodeflow.library.binding.*;
import thito.nodeflow.library.ui.*;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.regex.*;

public class Dialogs {

    private static boolean openNewProject, openCreateProject;

    public static List<File> importFiles(ProjectFacet facet, Window owner, I18nItem title) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(I18n.$("extension-supported").getString(),
                NodeFlow.getApplication().getEditorManager().getEditors().stream().map(x -> "*."+x.getExtension()).toArray(String[]::new)));
        for (FileHandler handler : NodeFlow.getApplication().getEditorManager().getEditors()) {
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(handler.getName(), "*."+handler.getExtension()));
        }
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));
        chooser.setInitialDirectory(new File(NodeFlow.getApplication().getConfiguration(facet).getString("initial-import-directory")).getAbsoluteFile());
        chooser.titleProperty().bind(title.stringBinding());
        List<File> files = chooser.showOpenMultipleDialog(owner.impl_getPeer());
        if (files != null && files.size() > 0 && files.get(0).getParent() != null) {
            NodeFlow.getApplication().getConfiguration(facet).set(files.get(0).getParent(), "initial-import-directory");
        }
        return files;
    }

    public static File exportProject(ProjectFacet facet, Window owner, I18nItem title) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Jar File", "*.jar"));
        chooser.setInitialDirectory(new File(NodeFlow.getApplication().getConfiguration(facet).getString("initial-export-directory")).getAbsoluteFile());
        chooser.titleProperty().bind(title.stringBinding());
        File export = chooser.showSaveDialog(owner.impl_getPeer());
        if (export != null && export.getParent() != null) {
            NodeFlow.getApplication().getConfiguration(facet).set(export.getParent(), "initial-export-directory");
        }
        return export;
    }

    public static void openEnumSelect(Window window, Class<?> javaClass, Consumer<Field> resultConsumer) {
        EnumSelectContent content = new EnumSelectContent(javaClass, resultConsumer);
        TextDialogButton button = DialogButton.createTextButton(0, 0, I18n.$("dialog-button-cancel"), null, event -> {
            event.close();
        });
        Dialog dialog = Dialog.createDialog(content, 0, button);
        dialog.open(window);
    }

    public static void openClassSelect(Window window, List<Class<?>> javaClasses, Consumer<Class<?>> resultConsumer) {
        ClassSelectContent content = new ClassSelectContent(javaClasses);
        TextDialogButton button = DialogButton.createTextButton(0, 0, I18n.$("dialog-button-cancel"), null, event -> {
            event.close();
        });
        Dialog dialog = Dialog.createDialog(content, 0, button);
        OpenedDialog openedDialog = dialog.open(window);
        content.selectedProperty().addListener((obs, old, val) -> {
            resultConsumer.accept(val);
            openedDialog.close(null);
        });
    }

    public static void openCharSelect(Window window, Consumer<Character> resultConsumer) {
        CharacterSelectContent content = new CharacterSelectContent(resultConsumer);
        TextDialogButton button = DialogButton.createTextButton(0, 0, I18n.$("dialog-button-cancel"), null, event -> {
            event.close();
        });
        Dialog dialog = Dialog.createDialog(content, 0, button);
        dialog.open(window);
    }

    public static OpenedDialog addNode(Window window, Project project, NodeFileSession session, double x, double y, boolean lazyLoad, boolean onlyEvents) {
        return addNode(window, project, session, x, y, lazyLoad, onlyEvents, null);
    }

    public static OpenedDialog addNode(Window window, Project project, NodeFileSession session, double x, double y, boolean lazyLoad, boolean onlyEvents, ObjectProperty<Node> prop) {
        NewNodeDialogContent content = new NewNodeDialogContent(project, session.getModule(), lazyLoad, onlyEvents);
        TextDialogButton button = DialogButton.createTextButton(0, 0, I18n.$("dialog-button-cancel"), null, event -> {
            event.close();
        });
        TextDialogButton button2 = DialogButton.createTextButton(1, 0, I18n.$("dialog-button-create"), null, event -> {
            Point2D location = session.impl_getViewport().getCanvas().getNodeContainer().screenToLocal(x, y);
            NodeProvider provider = content.providerProperty().get();
            Node node = provider.createComponent(session.getModule());
            node.getState().setX(location.getX());
            node.getState().setY(location.getY());
            ((NodeImpl) node).impl_getPeer().setLayoutX(location.getX());
            ((NodeImpl) node).impl_getPeer().setLayoutY(location.getY());
            session.getModule().nodes().add(node);
            if (prop != null) {
                prop.set(node);
            }
            event.close();
        });
        button2.impl_disableProperty().bind(content.providerProperty().isNull());
        Dialog dialog = Dialog.createDialog(content, 0, button, button2);
        return dialog.open(window);
    }

    public static void deleteFile(Window window, PhysicalResource[] resource) {
        ask(window, I18n.$("file-delete-title"), I18n.$("file-delete"), thito.nodeflow.api.ui.dialog.Dialog.Type.QUESTION, thito.nodeflow.api.ui.dialog.Dialog.Level.WARN, confirmed -> {
            if (confirmed) {
                for (PhysicalResource res : resource) {
                    res.moveToRecycleBin();
                }
            }
        });
    }

    private static boolean isValidName(String text) {
        Pattern pattern = Pattern.compile(
                "# Match a valid Windows filename (unspecified file system).          \n" +
                        "^                                # Anchor to start of string.        \n" +
                        "(?!                              # Assert filename is not: CON, PRN, \n" +
                        "  (?:                            # AUX, NUL, COM1, COM2, COM3, COM4, \n" +
                        "    CON|PRN|AUX|NUL|             # COM5, COM6, COM7, COM8, COM9,     \n" +
                        "    COM[1-9]|LPT[1-9]            # LPT1, LPT2, LPT3, LPT4, LPT5,     \n" +
                        "  )                              # LPT6, LPT7, LPT8, and LPT9...     \n" +
                        "  (?:\\.[^.]*)?                  # followed by optional extension    \n" +
                        "  $                              # and end of string                 \n" +
                        ")                                # End negative lookahead assertion. \n" +
                        "[^<>:\"/\\\\|?*\\x00-\\x1F]*     # Zero or more valid filename chars.\n" +
                        "[^<>:\"/\\\\|?*\\x00-\\x1F\\ .]  # Last char is not a space or dot.  \n" +
                        "$                                # Anchor to end of string.            ",
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.COMMENTS);
        Matcher matcher = pattern.matcher(text);
        boolean isMatch = matcher.matches();
        return isMatch;
    }

    public static void renameFile(EditorUI window, FileExplorer explorer, Resource old) {
        if (old == null) return;
        Project project = window.getWindow().getProject();
        DialogManager manager = NodeFlow.getApplication().getUIManager().getDialogManager();
        FormContent.StringForm fileName = FormContent.StringForm.create(I18n.$("dialog-file-name"), old.getName(), false);
        fileName.addValidator(new FormContent.Validator() {
            @Override
            public boolean validate(javafx.scene.Node node) {
                if (node instanceof TextInputControl) {
                    return isValidName(((TextInputControl) node).getText());
                }
                return true;
            }

            @Override
            public I18nItem getMessage() {
                return I18n.$("invalid-file-name");
            }
        });
        DialogButton button = manager.createTextButton(0, 0, I18n.$("dialog-button-cancel"), null, clickAction -> {
            clickAction.close();
        });
        DialogButton button2 = manager.createTextButton(1, 0, I18n.$("dialog-button-rename"), null, clickAction -> {
            if (old.getParentDirectory() != null && old instanceof PhysicalResource) {
                Resource resource;
                if (old instanceof ResourceFile) {
                    resource = old.getParentDirectory().getChild(fileName.getAnswer() + "." + ((ResourceFile) old).getExtension());
                } else {
                    resource = old.getParentDirectory().getChild(fileName.getAnswer());
                }
                ((PhysicalResource) old).moveTo(resource);
                explorer.getResourceTree().getSelectionModel().clearSelection();
            }
            clickAction.close();
        });
        BooleanBinding fileExistCheck;
        button2.impl_disableProperty().bind(fileExistCheck = Bindings.createBooleanBinding(() -> {
            if (fileName.getAnswer() == null || fileName.getAnswer().isEmpty() || !isValidName(fileName.getAnswer())) return true;
            if (old instanceof ResourceFile) {
                Resource resource = project.getSourceDirectory().getChild(fileName.getAnswer()+"."+((ResourceFile) old).getExtension());
                return !(resource instanceof UnknownResource);
            }
            return !(project.getSourceDirectory().getChild(fileName.getAnswer()) instanceof UnknownResource);
        }, fileName.impl_answerProperty()));
        fileName.addValidator(FormContent.Validator.property(fileExistCheck.not(), I18n.$("file-already-exist")));
        FormContent content = manager.createFormContent(I18n.$("dialog-rename-file"), Pos.CENTER, fileName);
        manager.createDialog(content, 0, button, button2).open(window.getWindow());
    }

    public static void createNewFile(EditorUI window, Project project, Object type) {
        DialogManager manager = NodeFlow.getApplication().getUIManager().getDialogManager();
        FormContent.StringForm fileName = FormContent.StringForm.create(I18n.$("dialog-file-name"), "", false);
        ArrayList<Object> choices = new ArrayList<>(NodeFlow.getApplication().getEditorManager().getEditors());
        choices.add(0, I18n.$("file-type-directory").getString());
        FormContent.ChoiceForm<Object> fileType = FormContent.ChoiceForm.create(I18n.$("dialog-file-type"), type,
                choices, false
                );
        fileName.addValidator(new FormContent.Validator() {
            @Override
            public boolean validate(javafx.scene.Node node) {
                if (node instanceof TextInputControl) {
                    Object type = fileType.getAnswer();
                    if (type instanceof FileHandler) {
                        if (!((FileHandler) type).isNameValid(((TextInputControl) node).getText())) {
                            return false;
                        }
                    }
                    return isValidName(((TextInputControl) node).getText());
                }
                return true;
            }

            @Override
            public I18nItem getMessage() {
                return I18n.$("invalid-file-name");
            }
        });
        DialogButton button = manager.createTextButton(0, 0, I18n.$("dialog-button-cancel"), null, clickAction -> {
            clickAction.close();
        });
        ResourceDirectory directory;
        TreeItem<Resource> selected = window.getExplorer().getResourceTree().getSelectionModel().getSelectedItem();
        if (selected != null && !(selected.getValue() instanceof ResourceDirectory)) {
            directory = selected.getValue().getParentDirectory();
        } else if (selected != null && selected.getValue() instanceof ResourceDirectory) {
            directory = (ResourceDirectory) selected.getValue();
        } else {
            directory = project.getSourceDirectory();
        }
        DialogButton button2 = manager.createTextButton(1, 0, I18n.$("dialog-button-create"), null, clickAction -> {
            Object answer = fileType.getAnswer();
            if (answer instanceof FileHandler) {
                Resource resource = directory.getChild(fileName.getAnswer()+"."+((FileHandler) fileType.getAnswer()).getExtension());
                ((UnknownResource) resource).createFile();
            } else {
                Resource resource = directory.getChild(fileName.getAnswer());
                ((UnknownResource) resource).createDirectory();
            }
            if (selected != null) {
                selected.setExpanded(true);
            }
            clickAction.close();
        });
        BooleanBinding fileExistCheck;
        button2.impl_disableProperty().bind(fileExistCheck = Bindings.createBooleanBinding(() -> {
            if (fileName.getAnswer() == null || fileType.getAnswer() == null || fileName.getAnswer().isEmpty() || !isValidName(fileName.getAnswer())) return true;
            if (fileType.getAnswer() instanceof FileHandler) {
                Resource resource = project.getSourceDirectory().getChild(fileName.getAnswer()+"."+((FileHandler) fileType.getAnswer()).getExtension());
                return !(resource instanceof UnknownResource) || !((FileHandler) fileType.getAnswer()).isNameValid(fileName.getAnswer());
            }
            return !(project.getSourceDirectory().getChild(fileName.getAnswer()) instanceof UnknownResource);
        }, fileName.impl_answerProperty(), fileType.impl_answerProperty()));
        fileName.addValidator(FormContent.Validator.property(fileExistCheck.not(), I18n.$("file-already-exist")));
        FormContent content = manager.createFormContent(I18n.$("dialog-create-new-file"), Pos.CENTER, fileName, fileType);
        manager.createDialog(content, 0, button, button2).open(window.getWindow());
    }

    public static void openFacetSelection(Window window, ProjectProperties properties, Runnable back) {
        DialogManager manager = NodeFlow.getApplication().getUIManager().getDialogManager();
        AtomicReference<OpenedDialog> openedDialog = new AtomicReference<>();
        ActionContent.Action[] actions = Arrays.stream(NodeFlow.getApplication().getProjectManager().getFacets()).map(x -> ActionContent.Action.createAction(I18n.direct(x.getName()), x.getIcon(), Task.createTask("facet-select", () -> {
            openedDialog.get().close(null);
            properties.setFacet(x);
            File file = new File(NodeFlow.getApplication().getSettings().<File>getValue(ApplicationSettings.WORKSPACE_DIRECTORY), properties.getName()+"/project.yml");
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            NodeFlow.getApplication().getProjectManager().storeProjectProperties(properties, (WritableResourceFile) ResourceManagerImpl.fileToResource(file));
            Project project = NodeFlow.getApplication().getProjectManager().loadProject(properties);
            UIManagerImpl.getInstance().getWindowsManager().getLauncher().getProjectsPage().refreshProjects();
            Task.runOnForeground("initialize-editor-window", () -> {
                project.getEditorWindow().show();
            });
        }, TaskThread.BACKGROUND), true)).toArray(ActionContent.Action[]::new);
        ActionContent content = manager.createActionContent(I18n.$("project-select-facet"), Pos.CENTER, actions);
        TextDialogButton backButton = manager.createTextButton(1, 0, I18n.$("dialog-button-back"), null, clickAction -> {
            clickAction.close();
            back.run();
        });
        openedDialog.set(manager.createDialog(content, 0, backButton).open(window));
    }

    public static void deleteProject(Window window, ProjectProperties properties) {
        DialogManager dialogManager = NodeFlow.getApplication().getUIManager().getDialogManager();
        DialogContent dialogContent = dialogManager.createMessageContent(
                thito.nodeflow.api.ui.dialog.Dialog.Type.QUESTION,
                thito.nodeflow.api.ui.dialog.Dialog.Level.WARN,
                I18n.$("project-delete-title"),
                Pos.LEFT,
                I18n.direct(I18n.$("project-delete-ask").getString(properties.getName())),
                Pos.LEFT);
        TextDialogButton dialogButton1 = dialogManager.createTextButton(1, DialogButton.DEFAULT_BUTTON, I18n.$("button-delete"), null, clickAction -> {
            clickAction.close();
            Project loaded = NodeFlow.getApplication().getProjectManager().getProject(properties);
            if (loaded != null) {
                inform(window,I18n.$("unable-to-delete-project"), I18n.$("loaded-project-cannot-be-deleted"), thito.nodeflow.api.ui.dialog.Dialog.Type.INFO, thito.nodeflow.api.ui.dialog.Dialog.Level.DANGER, null);
                return;
            }
            ((PhysicalResource) properties.getDirectory()).moveTo(properties.getDirectory().getParentDirectory().getChild("$RecycleBin/"+properties.getDirectory().getName()));
        });
        TextDialogButton dialogButton2 = dialogManager.createTextButton(2, 0, I18n.$("dialog-button-no"), null, clickAction -> {
            clickAction.close();
        });
        thito.nodeflow.api.ui.dialog.Dialog exitDialog = dialogManager.createDialog(dialogContent, thito.nodeflow.api.ui.dialog.Dialog.OVERLAY_CLOSE,
                dialogButton2, dialogButton1);
        exitDialog.open(window);
    }

    public static void editProjectProperties(Window window, ProjectProperties properties) {
        DialogManager manager = NodeFlow.getApplication().getUIManager().getDialogManager();
        FormContent.StringForm projectName = FormContent.StringForm.create(I18n.$("dialog-project-name"), properties.getName(), false);
        projectName.setRule("[-_A-Za-z0-9 ]+");
        FormContent.StringForm projectAuthor = FormContent.StringForm.create(I18n.$("dialog-project-author"), properties.getAuthor(), false);
        FormContent content = manager.createFormContent(I18n.$("project-edit"), Pos.CENTER,
                projectName, projectAuthor
        );
        DialogButton cancel = manager.createTextButton(0, DialogButton.DEFAULT_BUTTON, I18n.$("dialog-button-cancel"), null, clickAction -> {
            clickAction.close();
        });
        DialogButton next = manager.createTextButton(1, 0, I18n.$("button-apply"), null, clickAction -> {
            properties.rename(projectName.getAnswer());
            properties.setAuthor(projectAuthor.getAnswer());
            Task.runOnBackground("save-properties", () -> {
                properties.save();
            });
            clickAction.close();
        });
        thito.nodeflow.api.ui.dialog.Dialog dialog = manager.createDialog(content, 0, cancel, next);
        dialog.open(window);
    }

    public static void openNewProject(Window window, ProjectProperties old) {
        if (openNewProject) return;
        openNewProject = true;
        DialogManager manager = NodeFlow.getApplication().getUIManager().getDialogManager();
        FormContent.StringForm projectName = FormContent.StringForm.create(I18n.$("dialog-project-name"), old == null ? "" : old.getName(), false);
        projectName.setRule("[-_A-Za-z0-9 ]+");
        FormContent.StringForm projectAuthor = FormContent.StringForm.create(I18n.$("dialog-project-author"), old == null ? System.getProperty("user.name") : old.getAuthor(), false);
        FormContent content = manager.createFormContent(I18n.$("dialog-new-project-title"), Pos.CENTER,
                    projectName, projectAuthor
                );
        DialogButton cancel = manager.createTextButton(0, DialogButton.DEFAULT_BUTTON, I18n.$("dialog-button-cancel"), null, clickAction -> {
            clickAction.close();
        });
        DialogButton next = manager.createTextButton(1, 0, I18n.$("dialog-button-next"), null, clickAction -> {
            File file = new File(NodeFlow.getApplication().getSettings().<File>getValue(ApplicationSettings.WORKSPACE_DIRECTORY), projectName.getAnswer());
            file.mkdirs();
            ProjectProperties projectProperties = new ProjectPropertiesImpl(projectName.getAnswer(), projectAuthor.getAnswer(), (ResourceDirectory) ResourceManagerImpl.fileToResource(file), null, System.currentTimeMillis());
            openFacetSelection(window, projectProperties, () -> openNewProject(window, projectProperties));
            clickAction.close();
        });
        BooleanBinding binding;
        next.impl_disableProperty().bind(ConvertProperty.convertToString(projectName.impl_answerProperty()).isEmpty().or(ConvertProperty.convertToString(projectAuthor.impl_answerProperty()).isEmpty()).or(
                binding = Bindings.createBooleanBinding(() -> new File(NodeFlow.getApplication().getSettings().<File>getValue(ApplicationSettings.WORKSPACE_DIRECTORY), projectName.getAnswer()).exists(), projectName.impl_answerProperty())
        ));
        projectName.addValidator(FormContent.Validator.property(binding.not(), I18n.$("project-already-exists-in-directory")));
        thito.nodeflow.api.ui.dialog.Dialog dialog = manager.createDialog(content, 0, cancel, next);
        dialog.open(window).getActor().andThen(dialogButton -> openNewProject = false);
    }

    public static void openCreateDialog(Window window) {
        if (openCreateProject) return;
        openCreateProject = true;
        DialogManager manager = NodeFlow.getApplication().getUIManager().getDialogManager();
        DialogContent content = manager.createActionContent(I18n.$("dialog-title-add-project"), Pos.CENTER,
                manager.createAction(I18n.$("dialog-button-create-project"),
                        NodeFlow.getApplication().getResourceManager().getIcon("project-create"),
                        Task.createTask("project-create", () -> {
                            openNewProject(window, null);
                        }, TaskThread.FOREGROUND),
                        true
                    ),
                manager.createAction(I18n.$("dialog-button-import-project"),
                        NodeFlow.getApplication().getResourceManager().getIcon("project-load"),
                        Task.createTask("project-import", () -> {
                        }, TaskThread.FOREGROUND),
                        true
                    )
        );
        TextDialogButton button = manager.createTextButton(0, DialogButton.DEFAULT_BUTTON, I18n.$("dialog-button-cancel"), null, clickAction -> {
            clickAction.close();
        });
        // this is supposed to be a singleton, but nvm
        thito.nodeflow.api.ui.dialog.Dialog dialog = manager.createDialog(content, 0, button);
        dialog.open(window).getActor().andThen(x -> {
            openCreateProject = false;
        });
    }

    public static void openExitDialog(Window window) {
        if (window.impl_getPeer().getProperties().getOrDefault("close-request", false).equals(true)) return;
        window.impl_getPeer().getProperties().put("close-request", true);
        int openedWindows = NodeFlow.getApplication().getUIManager().getOpenedWindows().size();
        DialogManager dialogManager = NodeFlow.getApplication().getUIManager().getDialogManager();
        String type = openedWindows <= 1 ? "exit" : "close";
        DialogContent dialogContent = dialogManager.createMessageContent(
                thito.nodeflow.api.ui.dialog.Dialog.Type.QUESTION,
                thito.nodeflow.api.ui.dialog.Dialog.Level.WARN,
                I18n.$("dialog-title-"+type),
                Pos.LEFT,
                I18n.$("dialog-message-"+type),
                Pos.LEFT);
        CheckBoxDialogButton dialogButton = dialogManager.createCheckBoxButton(0, 0, I18n.$("dialog-button-skip"), null);
        TextDialogButton dialogButton1 = dialogManager.createTextButton(1, DialogButton.DEFAULT_BUTTON, I18n.$("dialog-button-yes"), null, clickAction -> {
            window.forceClose();
        });
        TextDialogButton dialogButton2 = dialogManager.createTextButton(2, 0, I18n.$("dialog-button-no"), null, clickAction -> {
            clickAction.getDialog().close(clickAction.getDialogButton());
        });
        dialogButton.impl_checkedProperty().addListener((obs, old, val) -> {
            NodeFlow.getApplication().getSettings().<Boolean>get(ApplicationSettings.ASK_BEFORE_EXIT).setValue(!val);
        });
        dialogButton2.impl_disableProperty().bind(dialogButton.impl_checkedProperty());
        thito.nodeflow.api.ui.dialog.Dialog exitDialog = dialogManager.createDialog(dialogContent, thito.nodeflow.api.ui.dialog.Dialog.OVERLAY_CLOSE,
                dialogButton, dialogButton1, dialogButton2);
        exitDialog.open(window).getActor().andThen(x -> {
            window.impl_getPeer().getProperties().remove("close-request");
        });
    }

    public static void ask(Window window, I18nItem title, I18nItem question, thito.nodeflow.api.ui.dialog.Dialog.Type type, thito.nodeflow.api.ui.dialog.Dialog.Level level, Consumer<Boolean> result) {
        DialogManager dialogManager = NodeFlow.getApplication().getUIManager().getDialogManager();
        DialogContent dialogContent = dialogManager.createMessageContent(
                type,
                level,
                title,
                Pos.LEFT,
                question,
                Pos.LEFT);
        TextDialogButton dialogButton1 = dialogManager.createTextButton(1, DialogButton.DEFAULT_BUTTON, I18n.$("dialog-button-yes"), null, clickAction -> {
            result.accept(true);
            clickAction.close();
        });
        TextDialogButton dialogButton2 = dialogManager.createTextButton(2, 0, I18n.$("dialog-button-no"), null, clickAction -> {
            result.accept(false);
            clickAction.close();
        });
        thito.nodeflow.api.ui.dialog.Dialog exitDialog = dialogManager.createDialog(dialogContent, thito.nodeflow.api.ui.dialog.Dialog.OVERLAY_CLOSE,
                dialogButton2, dialogButton1);
        exitDialog.open(window);
    }

    public static void inform(Window window, I18nItem title, I18nItem question, thito.nodeflow.api.ui.dialog.Dialog.Type type, thito.nodeflow.api.ui.dialog.Dialog.Level level, Runnable result) {
        DialogManager dialogManager = NodeFlow.getApplication().getUIManager().getDialogManager();
        DialogContent dialogContent = dialogManager.createMessageContent(
                type,
                level,
                title,
                Pos.LEFT,
                question,
                Pos.LEFT);
        TextDialogButton dialogButton1 = dialogManager.createTextButton(1, DialogButton.DEFAULT_BUTTON, I18n.$("button-ok"), null, clickAction -> {
            clickAction.close();
            if (result != null) {
                result.run();
            }
        });
        thito.nodeflow.api.ui.dialog.Dialog exitDialog = dialogManager.createDialog(dialogContent, Dialog.OVERLAY_CLOSE,
                dialogButton1);
        exitDialog.open(window);
    }

}
