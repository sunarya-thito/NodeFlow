package thito.nodeflow.ui.editor;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Bounds;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.dockfx.DockPane;
import thito.nodeflow.binding.ThreadBinding;
import thito.nodeflow.editor.Editor;
import thito.nodeflow.language.I18n;
import thito.nodeflow.plugin.PluginManager;
import thito.nodeflow.project.Project;
import thito.nodeflow.project.module.FileModule;
import thito.nodeflow.resource.Resource;
import thito.nodeflow.resource.ResourceType;
import thito.nodeflow.task.TaskThread;
import thito.nodeflow.ui.Component;
import thito.nodeflow.ui.FormDialog;
import thito.nodeflow.ui.Skin;
import thito.nodeflow.ui.dashboard.DashboardWindow;
import thito.nodeflow.ui.form.Validator;
import thito.nodeflow.ui.form.internal.CreateFileForm;
import thito.nodeflow.ui.form.internal.RenameResourceForm;

import java.io.File;

public class EditorSkin extends Skin {

    @Component("file-menu")
    Menu fileMenu;

    @Component("maximize-button")
    Button maximizeButton;

    @Component("file-close-project")
    MenuItem closeProject;

    @Component("file-import")
    MenuItem fileImport;

    @Component("file-export")
    MenuItem fileExport;

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

    @Component("file-create")
    Menu newFile;

    @Component("editor-content")
    DockPane editorContent;

    EditorFilePanelSkin filePanel = new EditorFilePanelSkin(this);
    EditorStructurePanelSkin structurePanel = new EditorStructurePanelSkin(this);
    EditorPluginPanelSkin pluginPanel = new EditorPluginPanelSkin(this);

    private SearchPopup searchPopup;
    private int menuIndex;
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
        registerActionHandler("project.close", ActionEvent.ACTION, event -> {
            editorWindow.getEditor().closeProject();
        });
        registerActionHandler("project.import", ActionEvent.ACTION, event -> {
        });
        registerActionHandler("project.export", ActionEvent.ACTION, event -> {

        });
    }

    public DockPane getEditorContent() {
        return editorContent;
    }

    @Override
    protected void onLayoutLoaded() {
        newFile.disableProperty().bind(editorWindow.getEditor().projectProperty().isNull());
        fileImport.disableProperty().bind(editorWindow.getEditor().projectProperty().isNull());
        fileExport.disableProperty().bind(editorWindow.getEditor().projectProperty().isNull());
        closeProject.disableProperty().bind(editorWindow.getEditor().projectProperty().isNull());
        for (FileModule module : PluginManager.getPluginManager().getModuleList()) {
            MenuItem menuItem = new MenuItem();
            menuItem.setGraphic(new ImageView(module.getIcon()));
            menuItem.textProperty().bind(module.getDisplayName());
            menuItem.addEventHandler(ActionEvent.ACTION, event -> {
                TreeItem<Resource> selected = filePanel.getExplorerView().getSelectionModel().getSelectedItem();
                Resource root = selected != null ? selected.getValue() : null;
                if (root == null) root = getEditorWindow().getEditor().projectProperty().get().getSourceFolder();
                showCreateFileForm(editorWindow.getEditor().projectProperty().get(), module, root);
            });
            newFile.getItems().add(menuItem);
        }
        searchPopup = new SearchPopup();
        Menu parent = moduleMenu.getParentMenu();
        if (parent != null) {
            menuIndex = moduleMenu.getParentMenu().getItems().indexOf(moduleMenu);
            moduleMenu.getParentMenu().getItems().remove(menuIndex);
        } else {
            menuIndex = menuBar.getMenus().indexOf(moduleMenu);
            menuBar.getMenus().remove(menuIndex);
        }


        searchField.textProperty().addListener((obs, old, val) -> {
            updateSearch(val);
        });

        searchBar.layoutBoundsProperty().addListener(obs -> updateSearchPopupPosition());
        getScene().getWindow().xProperty().addListener(obs -> updateSearchPopupPosition());
        getScene().getWindow().yProperty().addListener(obs -> updateSearchPopupPosition());
        getScene().getWindow().widthProperty().addListener(obs -> updateSearchPopupPosition());
        getScene().getWindow().heightProperty().addListener(obs -> updateSearchPopupPosition());
        updateSearchPopupPosition();

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

    public static void showRenameFileForm(FileModule m, Resource resource) {
        RenameResourceForm form = new RenameResourceForm();
        form.newName.set(resource.getName());
        form.newName.validate(Validator.<String>mustNotEmpty()
                .combine(Validator.validFilename())
                .combine(Validator.resourceMustNotExist().map(value -> {
                    String path = value;
                    if (m != null) {
                        String extension = m.getExtension();
                        if (extension != null) {
                            path += "." + extension;
                        }
                    }
                    return resource.getParent().getChild(path);
                })));
        FormDialog.create(I18n.$("dialogs.rename-file.title"), form).show(result -> {
            if (result != null) {
                String path = result.newName.get();
                if (m != null) {
                    String extension = m.getExtension();
                    if (extension != null) {
                        path += "." + extension;
                    }
                }
                resource.toFile().renameTo(resource.getParent().getChild(path).toFile());
            }
        });
    }

    public static void showCreateFileForm(Project project, FileModule module, Resource root) {
        if (root.getType() == ResourceType.FILE) {
            root = root.getParent();
        }
        CreateFileForm form = new CreateFileForm();
        Validator<Resource> resourceValidator = module.getFileValidator();
        Validator<String> existenceValidator = value -> {
            String path = form.name.get();
            FileModule m = form.type.get();
            if (m != null) {
                String extension = m.getExtension();
                if (extension != null) {
                    path += "." + extension;
                }
            }
            path = path.trim();
            String dir = form.directory.get();
            if (dir != null && !(dir = dir.trim()).isEmpty()) {
                path = new File(dir, path).getPath();
            }
            return resourceValidator.validate(project.getSourceFolder().getChild(path));
        };
        form.directory.set(root.getPath(project.getSourceFolder()));
        form.directory.validate(Validator.validFilename()
                .combine(Validator.pathNotExist().map(x -> project.getSourceFolder().getChild(x))));
        form.name.validate(
                Validator.combine(
                        Validator.combine(
                                Validator.mustNotEmpty(),
                                Validator.validFilename()),
                        existenceValidator));
        form.type.validate(Validator.mustNotEmpty());
        form.type.set(module);
        FormDialog.create(I18n.$("dialogs.new-file.title"), form).show(result -> {
            if (result != null) {
                String path = result.name.get();
                String extension = result.type.get().getExtension();
                if (extension != null) {
                    path += "." + extension;
                }
                String dir = form.directory.get();
                if (dir != null && !(dir = dir.trim()).isEmpty()) {
                    path = new File(dir, path).getPath();
                }
                Resource newFile = project.getSourceFolder().getChild(path);
                result.type.get().createFile(newFile);
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
//        SearchQuery query = new SearchQuery(string);
//        SearchThread.submit(() -> {
//            for (SearchableContentContext context : editor.getSearchableContentContexts()) {
//                for (SearchableContent content : context.getSearchableContentList()) {
//                    SearchSession searchSession = content.search(query);
//                    if (searchSession == null) continue;
//                    for (SearchResult result : searchSession.getResults()) {
//                        SearchResultItem item = new SearchResultItem(result.getTitle(), I18n.$("search-source").format(searchSession.getName()), result);
//                        ObservableValue<String> iconUrl = context.getProvider().iconURLProperty();
//                        if (iconUrl != null) item.iconProperty().bind(Bindings.createObjectBinding(() -> new Image(iconUrl.getValue()), iconUrl));
//                        TaskThread.UI().schedule(() -> {
//                            searchPopup.getSearchResultItems().add(item);
//                        });
//                    }
//                }
//            }
//        });
    }

    public int getMenuIndex() {
        return menuIndex;
    }

    public MenuBar getMenuBar() {
        return menuBar;
    }
}
