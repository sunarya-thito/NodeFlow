package thito.nodeflow.ui.editor;

import javafx.application.Platform;
import javafx.collections.ObservableSet;
import javafx.event.ActionEvent;
import javafx.geometry.Bounds;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import thito.nodeflow.binding.MappedListBinding;
import thito.nodeflow.binding.ThreadBinding;
import thito.nodeflow.language.I18n;
import thito.nodeflow.plugin.PluginManager;
import thito.nodeflow.project.Project;
import thito.nodeflow.project.ProjectManager;
import thito.nodeflow.project.module.FileModule;
import thito.nodeflow.resource.Resource;
import thito.nodeflow.resource.ResourceType;
import thito.nodeflow.task.TaskThread;
import thito.nodeflow.ui.Component;
import thito.nodeflow.ui.FormDialog;
import thito.nodeflow.ui.Skin;
import thito.nodeflow.ui.dashboard.DashboardWindow;
import thito.nodeflow.ui.docker.DockerComponent;
import thito.nodeflow.ui.docker.DockerManager;
import thito.nodeflow.ui.docker.DockerPane;
import thito.nodeflow.ui.docker.DockerTab;
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
    BorderPane mainViewport;

    @Component("file-create")
    Menu newFile;

    @Component("tool-window")
    Menu toolWindow;

//    EditorFilePanelSkin filePanel = new EditorFilePanelSkin(this);
//    EditorStructurePanelSkin structurePanel = new EditorStructurePanelSkin(this);
//    EditorPluginPanelSkin pluginPanel = new EditorPluginPanelSkin(this);

    private SearchPopup searchPopup;
    private int menuIndex;
    private EditorWindow editorWindow;

    private DockerPane dockerPane;

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
            editorWindow.getContext().getTaskQueue().executeBatch(ProjectManager.getInstance().closeProject(editorWindow.getContext()));
        });
        registerActionHandler("project.import", ActionEvent.ACTION, event -> {
        });
        registerActionHandler("project.export", ActionEvent.ACTION, event -> {
        });
        registerActionHandler("editor.resetLayout", ActionEvent.ACTION, event -> {});
    }

    @Override
    protected void onLayoutLoaded() {
        for (FileModule module : PluginManager.getPluginManager().getModuleList()) {
            MenuItem menuItem = new MenuItem();
            ImageView node = new ImageView();
            node.imageProperty().bind(module.iconProperty());
            menuItem.setGraphic(node);
            menuItem.textProperty().bind(module.getDisplayName());
            menuItem.addEventHandler(ActionEvent.ACTION, event -> {
                ObservableSet<Resource> selected = editorWindow.getContext().getSelectedFiles();
                Resource root = selected.isEmpty() ? getEditorWindow().getContext().getProject().getSourceFolder() :
                        selected.stream().findFirst().orElse(null);
                showCreateFileForm(getEditorWindow().getContext().getProject(), module, root);
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

        MappedListBinding.bind(toolWindow.getItems(), DockerManager.getManager().getDockerComponentList().filtered(DockerComponent::isMenuAccessible), component -> {
            MenuItem menuItem = new MenuItem();
            menuItem.textProperty().bind(component.displayName());
            menuItem.setOnAction(event -> {
                if (!component.allowMultipleView()) {
                    editorWindow.getContext().contains(component, result -> {
                        if (!result) {
                            TaskThread.UI().schedule(() -> {
                                editorWindow.getDockerPane().getTabs(component.getDefaultPosition()).getTabList()
                                        .add(new DockerTab(editorWindow.getContext().getDockerContext(), component.createDockNode(editorWindow.getContext(), null)));
                            });
                        }
                    });
                } else {
                    TaskThread.UI().schedule(() -> {
                        editorWindow.getDockerPane().getTabs(component.getDefaultPosition()).getTabList()
                                .add(new DockerTab(editorWindow.getContext().getDockerContext(), component.createDockNode(editorWindow.getContext(), null)));
                    });
                }
            });
            return menuItem;
        });

        searchField.textProperty().addListener((obs, old, val) -> {
            updateSearch(val);
        });

        searchBar.layoutBoundsProperty().addListener(obs -> updateSearchPopupPosition());
        getScene().getWindow().xProperty().addListener(obs -> updateSearchPopupPosition());
        getScene().getWindow().yProperty().addListener(obs -> updateSearchPopupPosition());
        getScene().getWindow().widthProperty().addListener(obs -> updateSearchPopupPosition());
        getScene().getWindow().heightProperty().addListener(obs -> updateSearchPopupPosition());
        updateSearchPopupPosition();

        ThreadBinding.bind(fileMenu.textProperty(), editorWindow.getContext().getProject().getProperties().nameProperty(), TaskThread.IO(), TaskThread.UI());

        dockerPane = editorWindow.getDockerPane();
        mainViewport.setCenter(dockerPane);

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
                    String finalPath = path;
                    return TaskThread.IO().process(() ->  resource.getParent().getChild(finalPath));
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
                String finalPath = path;
                resource.toFile().renameTo(TaskThread.IO().process(() -> resource.getParent().getChild(finalPath)).toFile());
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
            String finalPath = path;
            return resourceValidator.validate(TaskThread.IO().process(() -> project.getSourceFolder().getChild(finalPath)));
        };
        form.directory.set(root.getPath(project.getSourceFolder()));
        form.directory.validate(Validator.validFilename()
                .combine(Validator.mustNotFile().map(x -> x.isEmpty() ? project.getSourceFolder() : TaskThread.IO().process(() -> project.getSourceFolder().getChild(x)))));
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
                String finalPath = path;
                TaskThread.IO().schedule(() -> {
                    Resource newFile = project.getSourceFolder().getChild(finalPath);
                    result.type.get().createFile(newFile);
                });
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
