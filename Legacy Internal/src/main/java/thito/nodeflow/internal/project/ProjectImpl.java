package thito.nodeflow.internal.project;

import javafx.collections.*;
import thito.nodeflow.api.config.*;
import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.project.*;
import thito.nodeflow.api.resource.*;
import thito.nodeflow.api.task.*;
import thito.nodeflow.api.ui.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.internal.editor.config.*;
import thito.nodeflow.internal.editor.record.*;
import thito.nodeflow.internal.node.*;
import thito.nodeflow.internal.ui.editor.*;

import java.io.*;
import java.util.*;

public class ProjectImpl implements Project {

    private final ProjectProperties projectProperties;
    private EditorWindowImpl editorWindow;
    private Section configuration;
    private ObservableList<ProjectDebugger> debuggers = FXCollections.observableArrayList();
    private DebuggerListener debuggerListener = new DebuggerListener() {
        @Override
        public void onDebuggerTerminated(ProjectDebugger debugger) {
            debuggers.remove(debugger);
        }
    };

    private ObservableList<RecordFileCategoryProvider> recordProviders = FXCollections.observableArrayList();
    private ObservableList<ConfigFileCategoryProvider> variableProviders = FXCollections.observableArrayList();
    private Set<String> yamlPaths = new HashSet<>();

    public ProjectImpl(ProjectProperties projectProperties) {
        this.projectProperties = projectProperties;
        Resource resource = projectProperties.getDirectory().getChild("facet.yml");
        if (resource instanceof UnknownResource) {
            resource = ((UnknownResource) resource).createFile();
        }
        if (resource instanceof ResourceFile) {
            configuration = Section.loadYaml((ResourceFile) resource);
        }
        if (configuration == null) {
            configuration = Section.newMap();
        }
        variableProviders.addListener((ListChangeListener<ConfigFileCategoryProvider>) c -> {
            while (c.next()) {
                if (c.wasRemoved()) {
                    ModuleManagerImpl.getInstance().getCategories().removeAll(c.getRemoved());
                }
                if (c.wasAdded()) {
                    ModuleManagerImpl.getInstance().getCategories().addAll(c.getAddedSubList());
                }
            }
        });
        recordProviders.addListener((ListChangeListener<? super RecordFileCategoryProvider>) c -> {
            while (c.next()) {
                if (c.wasRemoved()) {
                    ModuleManagerImpl.getInstance().getCategories().removeAll(c.getRemoved());
                }
                if (c.wasAdded()) {
                    ModuleManagerImpl.getInstance().getCategories().addAll(c.getAddedSubList());
                }
            }
        });
        refreshVariables();
        refreshRecords();
        refreshYaml();
        Task.runOnForeground("construct-editor-window", () -> {
            editorWindow = new EditorWindowImpl(this);
        });
    }

    public Set<String> getYamlPaths() {
        return yamlPaths;
    }

    public void refreshYaml() {
        Set<ResourceFile> files = new HashSet<>();
        collectYaml(files, getSourceDirectory());
        Set<String> path = new HashSet<>();
        for (ResourceFile file : files) {
            try {
                Section section = Section.loadYaml(file);
                if (section != null) {
                    path.addAll(section.collectMapKeys(true));
                }
            } catch (Throwable t) {
            }
        }
        yamlPaths = path;
    }

    public void refreshRecords() {
        Set<ResourceFile> files = new HashSet<>();
        collectRecords(files, getSourceDirectory());
        Set<RecordFileCategoryProvider> existingCategories = new HashSet<>();
        for (ResourceFile file : files) {
            RecordFileModule module = new RecordFileModule(file.getName(), file, true);
            try (InputStream inputStream = file.openInput()) {
                module.load(inputStream);
                RecordFileCategoryProvider category = null;
                for (RecordFileCategoryProvider cat : recordProviders) {
                    if (cat.getFile().getPath().equals(file.getPath())) {
                        category = cat;
                        break;
                    }
                }
                if (category == null) {
                    recordProviders.add(category = new RecordFileCategoryProvider(getProperties().getName(), file));
                }
                existingCategories.add(category);
                //
                List<NodeProvider> existingProvider = new ArrayList<>();
                DefaultCreateRecordNodeProvider c = new DefaultCreateRecordNodeProvider(module, category);
                existingProvider.add(getIfNotExist(category.getProviders(), c));
//                disabled for reason:
//                every changes to the record will make the user have to recreate their node again
//                if (module.getItems().size() > 0) {
//                    CreateRecordNodeProvider d = new CreateRecordNodeProvider(module, category);
//                    existingProvider.add(getIfNotExist(category.getProviders(), d));
//                }
                for (RecordItem item : module.getItems()) {
                    GetRecordItemNodeProvider a = new GetRecordItemNodeProvider(item, category);
                    SetRecordItemNodeProvider b = new SetRecordItemNodeProvider(item, category);
                    existingProvider.add(getIfNotExist(category.getProviders(), a));
                    existingProvider.add(getIfNotExist(category.getProviders(), b));
                }
                category.getProviders().clear();
                category.getProviders().addAll(existingProvider);
            } catch (Throwable t) {
            }
        }
        recordProviders.retainAll(existingCategories);
    }

    /*
    Trigger when:
    Creating cfg file
    Deleting cfg file
    Modifying cfg file
     */
    public void refreshVariables() {
        Set<ResourceFile> files = new HashSet<>();
        collect(files, getSourceDirectory());
        Set<ConfigFileCategoryProvider> existingCategories = new HashSet<>();
        for (ResourceFile file : files) {
            ConfigFileModule module = new ConfigFileModule(file.getName());
            try (InputStream inputStream = file.openInput()) {
                module.load(inputStream);
                ConfigFileCategoryProvider category = null;
                for (ConfigFileCategoryProvider cat : variableProviders) {
                    if (cat.getFile().getPath().equals(file.getPath())) {
                        category = cat;
                        break;
                    }
                }
                if (category == null) {
                    variableProviders.add(category = new ConfigFileCategoryProvider(getProperties().getName(), file));
                }
                existingCategories.add(category);
                //
                List<NodeProvider> existingProvider = new ArrayList<>();
                for (ConfigFileModule.Value value : module.getValues()) {
                    ConfigValueType type = ConfigFileManager.getManager().getType(value.getType());
                    SetConfigNodeProvider a = new SetConfigNodeProvider(value.getID(), value.getName(), type.getFieldType(), category);
                    GetConfigNodeProvider b = new GetConfigNodeProvider(value.getID(), value.getName(), type.getFieldType(), category);
                    existingProvider.add(getIfNotExist(category.getProviders(), a));
                    existingProvider.add(getIfNotExist(category.getProviders(), b));
                }
                category.getProviders().clear();
                category.getProviders().addAll(existingProvider);
            } catch (Throwable t) {
            }
        }
        variableProviders.retainAll(existingCategories);
    }

    private static NodeProvider getIfNotExist(List<NodeProvider> providers, NodeProvider provider) {
        for (NodeProvider pr : providers) {
            if (pr.getID().equals(provider.getID())) {
                return pr;
            }
        }
        return provider;
    }

    // scan variables
    void collect(Set<ResourceFile> files, Resource resource) {
        if (resource instanceof ResourceDirectory) {
            for (Resource res : ((ResourceDirectory) resource).getChildren()) {
                collect(files, res);
            }
        } else if (resource instanceof ResourceFile) {
            if (((ResourceFile) resource).getExtension().equals("cfg")) {
                files.add((ResourceFile) resource);
            }
        }
    }

    void collectRecords(Set<ResourceFile> files, Resource resource) {
        if (resource instanceof ResourceDirectory) {
            for (Resource res : ((ResourceDirectory) resource).getChildren()) {
                collectRecords(files, res);
            }
        } else if (resource instanceof ResourceFile) {
            if (((ResourceFile) resource).getExtension().equals("rec")) {
                files.add((ResourceFile) resource);
            }
        }
    }
    void collectYaml(Set<ResourceFile> files, Resource resource) {
        if (resource instanceof ResourceDirectory) {
            for (Resource res : ((ResourceDirectory) resource).getChildren()) {
                collectYaml(files, res);
            }
        } else if (resource instanceof ResourceFile) {
            if (((ResourceFile) resource).getExtension().equals("yml") || ((ResourceFile) resource).getExtension().equals("yaml")) {
                files.add((ResourceFile) resource);
            }
        }
    }

    @Override
    public List<ProjectDebugger> getActiveDebugger() {
        return Collections.unmodifiableList(debuggers);
    }

    @Override
    public FutureSupplier<ProjectDebugger> runDebug() {
        CompletableFutureSupplier<ProjectDebugger> debuggerCompletableFutureSupplier = FutureSupplier.createCompletable();
        Resource resource = getProperties().getDirectory().getChild("bin/debug.jar");
        if (resource instanceof ResourceDirectory && resource instanceof PhysicalResource) {
            ((PhysicalResource) resource).delete();
        }
        if (resource instanceof UnknownResource) {
            resource = ((UnknownResource) resource).createFile();
        }
        try {
            if (!getFacet().hasDebugSupport()) {
                debuggerCompletableFutureSupplier.complete(null);
            } else {
                ProjectCompiler compiler = new ProjectCompiler(this, (WritableResourceFile) resource);
                Resource finalResource = resource;
                compiler.exportProject().andThen(result -> {
                    if (result) {
                        ProjectDebugger debugger = getFacet().runDebugger(this, debuggerListener, TaskThread.DEBUGGER, (ResourceFile) finalResource);
                        debuggers.add(debugger);
                        debuggerCompletableFutureSupplier.complete(debugger);
                    }
                }).andThenError(error -> {
                    debuggerCompletableFutureSupplier.error(error);
                });
            }
        } catch (Throwable t) {
            debuggerCompletableFutureSupplier.error(t);
        }
        return debuggerCompletableFutureSupplier;
    }

    public void close() {
        Resource resource = projectProperties.getDirectory().getChild("facet.yml");
        if (resource instanceof UnknownResource) {
            resource = ((UnknownResource) resource).createFile();
        }
        if (resource instanceof WritableResourceFile) {
            Section.saveYaml(configuration, (WritableResourceFile) resource);
        }
        for (ProjectDebugger debugger : getActiveDebugger()) {
            try {
                debugger.terminate();
            } catch (Throwable t) {
                debugger.submit(() -> {
                    debugger.terminate();
                });
            }
        }
        Toolkit.info("Project "+getProperties().getName()+" has been closed.");
    }

    @Override
    public Section getFacetConfiguration(ProjectFacet facet) {
        String name = facet.getName();
        if (configuration.has(name)) {
            return configuration.getMap(name);
        } else {
            Section map = Section.newMap();
            configuration.set(map, name);
            return map;
        }
    }

    @Override
    public EditorWindow getEditorWindow() {
        return editorWindow;
    }

    @Override
    public ProjectFacet getFacet() {
        return getProperties().getFacet();
    }

    @Override
    public ProjectProperties getProperties() {
        return projectProperties;
    }

}
