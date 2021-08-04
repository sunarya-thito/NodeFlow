package thito.nodeflow.internal;

import javafx.application.*;
import javafx.scene.text.*;
import org.controlsfx.glyphfont.*;
import org.objectweb.asm.*;
import org.slf4j.bridge.*;
import org.slf4j.impl.*;
import thito.nodeflow.api.*;
import thito.nodeflow.api.bundle.*;
import thito.nodeflow.api.config.*;
import thito.nodeflow.api.editor.*;
import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.event.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.node.eventbus.command.*;
import thito.nodeflow.api.project.*;
import thito.nodeflow.api.resource.*;
import thito.nodeflow.api.settings.*;
import thito.nodeflow.api.task.*;
import thito.nodeflow.api.ui.*;
import thito.nodeflow.api.ui.UIManager;
import thito.nodeflow.api.ui.dialog.*;
import thito.nodeflow.internal.bundle.*;
import thito.nodeflow.internal.editor.*;
import thito.nodeflow.internal.event.*;
import thito.nodeflow.internal.locale.*;
import thito.nodeflow.internal.node.*;
import thito.nodeflow.internal.node.eventbus.command.*;
import thito.nodeflow.internal.project.*;
import thito.nodeflow.internal.resource.*;
import thito.nodeflow.internal.settings.*;
import thito.nodeflow.internal.task.*;
import thito.nodeflow.internal.ui.*;
import thito.nodeflow.internal.ui.dialog.*;
import thito.nodeflow.internal.ui.popup.*;
import thito.nodeflow.library.ui.decoration.popup.*;
import thito.nodeflow.library.ui.injection.*;
import thito.nodeflow.minecraft.FXUtil;
import thito.nodejfx.*;
import uk.org.lidalia.sysoutslf4j.context.*;

import javax.swing.*;
import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.jar.*;
import java.util.logging.*;

public class NodeFlowImpl extends NodeFlow {

    static {
        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.uninstall();
//        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
    }

    public static NodeFlowImpl getInstance() {
        return (NodeFlowImpl) getApplication();
    }

    public static void initialize() {
        System.out.println("Initializing application...");
        FXUtil.poke();
        NodeContext.getMouseX();
        // Initialize Instance
        new NodeFlowImpl();
        Logger logger = NodeFlow.getApplication().getLogger();
        logger.setLevel(Level.FINEST);
        logger.addHandler(new Handler() {
            @Override
            public void publish(LogRecord record) {
                String log;
                Object[] params = record.getParameters();
                if (params != null) {
                    log = String.format(record.getMessage(), params);
                } else {
                    log = record.getMessage();
                }
                Throwable thrown = record.getThrown();
                if (thrown != null) {
                    StringWriter writer = new StringWriter();
                    thrown.printStackTrace(new PrintWriter(writer));
                    log += "\n" + writer;
                }

                String prefix = String.format("[%s][%s][%s] ", record.getLevel().getName(), new SimpleDateFormat("ss:mm:hh").format(new Date()), Thread.currentThread().getName());
                for (String s : log.split("\n")) {
                    System.out.println(prefix + s);
                }
            }

            @Override
            public void flush() {

            }

            @Override
            public void close() throws SecurityException {

            }
        });

        // Patch for Launcher
        GlyphFontRegistry.register(new FontAwesome());

        // Built-in Facets
//        getApplication().getEditorManager().registerHandler(new YamlEditor());

        Toolkit.info("Loading resources...");
        // Initialize fonts
        Resource fontsDir = NodeFlow.getApplication().getResourceManager().getResource("fonts");
        int count = 0;
        if (fontsDir instanceof ResourceDirectory) {
            for (Resource resource : ((ResourceDirectory) fontsDir).getChildren()) {
                if (resource instanceof ResourceFile) {
                    try (InputStream inputStream = ((ResourceFile) resource).openInput()) {
                        Font font = Font.loadFont(inputStream, 10);
                        if (font == null) {
                            logger.log(Level.WARNING, "Failed to load font "+((ResourceFile) resource).getFileName());
                        } else {
                            logger.log(Level.INFO, "Loaded font "+resource.getName()+" as "+font.getName());
                            count++;
                        }
                    } catch (Throwable t) {
                        throw new ReportedError(t);
                    }
                }
            }
        }
        logger.log(Level.INFO, "Loaded "+count+" fonts!");
        Listener.registerListener(new GeneralListener());
        StyleInjector.initialize();
        Statistic.STATS.run();
        PopupBase.getInvisibleParent(); // initialize
        NotificationPopup.initialize();
        logger.log(Level.INFO, "Loading configuration...");
        Resource resource = getInstance().getResourceManager().getExternalResource("locales/en.properties");
        if (resource instanceof ResourceFile && !(resource instanceof UnknownResource)) {
            getInstance().getLocaleManager().loadLocale((ResourceFile) resource);
        }
        Resource config = getApplication().getResourceManager().getExternalResource("config.yml");
        if (config instanceof ResourceFile && !(config instanceof UnknownResource)) {
            ((ApplicationSettingsImpl) getApplication().getSettings()).load((ResourceFile) config);
        }
        Task.runOnForeground("initialize-thito.nodeflow.installer.windows", () -> {
            UIManagerImpl.getInstance().getWindowsManager().initializeWindows();
        });
    }
    private final Logger logger = Logger.getLogger("NodeFlow");
    private BundleManager bundleManager = new BundleManagerImpl();
    private final ResourceManager resourceManager = new ResourceManagerImpl();
    private final LocaleManager localeManager = new LocaleManagerImpl();
    private final ApplicationSettings applicationSettings = new ApplicationSettingsImpl();
    private ProjectManager projectManager = new ProjectManagerImpl();
    private UIManager uiManager = new UIManagerImpl();
    private EventManager eventManager = new EventManagerImpl();
    private final TaskManager taskManager = new TaskManagerImpl();
    private EditorManager editorManager = new EditorManagerImpl();
    private Toolkit toolkit = new Toolkit();
    private Updater updater = new UpdaterImpl();
    private Section appYml;
    private Section facetConfig;
    private UUID deviceUUID;

    protected NodeFlowImpl() {
        appYml = Section.loadYaml((ResourceFile) resourceManager.getResource("app.yml"));
        Resource resource = resourceManager.getExternalResource("user.yml");
        if (resource instanceof UnknownResource) {
            resource = ((UnknownResource) resource).createFile();
        }
        if (resource instanceof ResourceFile) {
            facetConfig = Section.loadYaml((ResourceFile) resource);
        }
        if (facetConfig == null) {
            facetConfig = Section.newMap();
        }
        if (facetConfig.has("uuid")) {
            try {
                deviceUUID = UUID.fromString(facetConfig.getString("uuid"));
            } catch (Throwable t) {
            }
        }
        if (deviceUUID == null) {
            deviceUUID = UUID.randomUUID();
            facetConfig.set(deviceUUID.toString(), "uuid");
            Section.saveYaml(facetConfig, ((WritableResourceFile) resourceManager.getResource("user.yml")));
        }
        int eula = EULA.isEulaAccepted();
        if (eula != 1) {
            JOptionPane.showMessageDialog(null, new MessageWithLink("Please update the launcher using the installer. <br>More info at: <br><a href=\"https://github.com/sunarya-thito/NodeFlow/wiki/Updating-the-Launcher\">https://github.com/sunarya-thito/NodeFlow/wiki/Updating-the-Launcher</a>"), "Outdated Software", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
            return;
        }
        ((ApplicationSettingsImpl) getSettings()).initializeDefaultSettings();
        new Metrics(11658, deviceUUID);
    }

    @Override
    public CommandManager getCommandManager() {
        return CommandManagerImpl.getInstance();
    }

    public void loadBundles() {
        File file = ApplicationSettingsImpl.settings().getValue(ApplicationSettings.BUNDLES_DIRECTORY);
        File[] list = file.listFiles();
        if (list != null) {
            for (File fx : list) {
                File prop = new File(fx, "bundle.yml");
                Resource resource = ResourceManagerImpl.fileToResource(prop);
                if (resource instanceof ResourceFile) {
                    BundleProperties properties = getBundleManager().readBundleProperties((ResourceFile) resource);
                    Toolkit.info("Loading bundle: "+properties.getName()+" ("+properties.getId()+")");
                    properties.loadBundle();
                    Toolkit.info("Bundle loaded: "+properties.getName()+" "+properties.getVersion());
                }
            }
        }
    }

    public void loadFacets() {
        // Prepare facets
        Resource facetDir = getApplication().getResourceManager().getExternalResource("facets");
        if (facetDir instanceof UnknownResource) {
            ((UnknownResource) facetDir).createDirectory();
        } else if (facetDir instanceof ResourceDirectory) {
            Map<String, Map<String, FacetClassLoader>> loadSorted = new HashMap<>();
            for (Resource facet : ((ResourceDirectory) facetDir).getChildren()) {
                if (facet instanceof ResourceFile && facet instanceof PhysicalResource) {
                    if (((ResourceFile) facet).getExtension().equalsIgnoreCase("jar")) {
                        List<String> entries = new ArrayList<>();
                        try (JarFile file = new JarFile(facet.getPath())) {
                            Enumeration<JarEntry> entry = file.entries();
                            while (entry.hasMoreElements()) {
                                JarEntry x = entry.nextElement();
                                if (x.getName().endsWith(".class")) {
                                    entries.add(x.getName());
                                }
                            }
                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                        // Facet Class Loader inherits NodeFlow API Class Loader so it will never be able to access
                        // the internal classes
                        try {
                            FacetClassLoader facetClassLoader = new FacetClassLoader(new URL[]{((PhysicalResource) facet).getSystemPath().toUri().toURL()}, NodeFlow.class.getClassLoader());
                            for (String entry : entries) {
                                try (InputStream inputStream = facetClassLoader.getResourceAsStream(entry)) {
                                    Toolkit.info("Loading class: "+entry);
                                    ClassReader reader = new ClassReader(inputStream);
                                    String superName = reader.getSuperName();
                                    Toolkit.info("Identified class: "+reader.getClassName()+" with dependency: "+superName);
                                    String className = entry.substring(0, entry.length() - 6).replace('/', '.');
                                    loadSorted.computeIfAbsent(superName.replace('/', '.'), x -> new HashMap<>()).put(className, facetClassLoader);
                                } catch (Throwable t) {
                                    t.printStackTrace();
                                }
                            }
                        } catch (MalformedURLException e) {
                            Toolkit.error("Cannot load facet "+facet.getName(), e);
                        }
                    }
                }
            }
            linkedLoad(loadSorted, "java.lang.Object");
        }
    }

    private void linkedLoad(Map<String, Map<String, FacetClassLoader>> sorted, String dependency) {
        Map<String, FacetClassLoader> dependant = sorted.get(dependency);
        if (dependant != null) {
            if (dependency != null) Toolkit.info("Loading dependant facets from "+dependency+" ("+dependant.size()+")");
            dependant.forEach((entry, classLoader) -> {
                Toolkit.info("Initializing "+entry);
                try {
                    classLoader.initialize(entry);
                    linkedLoad(sorted, entry);
                } catch (Throwable t) {
                    Toolkit.error("Cannot load "+entry, t);
                }
            });
        }
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public EventManager getEventManager() {
        return eventManager;
    }

    @Override
    public Version getVersion() {
        return VersionImpl.parse(appYml.getString("version"));
    }

    @Override
    public Updater getUpdater() {
        return updater;
    }

    @Override
    public BundleManager getBundleManager() {
        return bundleManager;
    }

    @Override
    public ApplicationSettings getSettings() {
        return applicationSettings;
    }

    @Override
    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    @Override
    public LocaleManager getLocaleManager() {
        return localeManager;
    }

    @Override
    public ModuleManager getModuleManager() {
        return ModuleManagerImpl.getInstance();
    }

    @Override
    public ProjectManager getProjectManager() {
        return projectManager;
    }

    @Override
    public UIManager getUIManager() {
        return uiManager;
    }

    @Override
    public TaskManager getTaskManager() {
        return taskManager;
    }

    @Override
    public EditorManager getEditorManager() {
        return editorManager;
    }

    @Override
    public Toolkit getToolkit() {
        return toolkit;
    }

    @Override
    public void shutdown() {
        new Thread("SHUTDOWN") {
            @Override
            public void run() {
                try {
                    Toolkit.info("Shutting down application...");
                    for (Project project : getProjectManager().getLoadedProjects()) {
                        getProjectManager().unloadProject(project);
                    }
                    Toolkit.info("Saving configuration...");
                    Resource config = getResourceManager().getExternalResource("config.yml");
                    if (config instanceof UnknownResource) {
                        config = ((UnknownResource) config).createFile();
                    }
                    if (config instanceof WritableResourceFile) {
                        ((ApplicationSettingsImpl) getSettings()).save((WritableResourceFile) config);
                    }
                    config = getResourceManager().getExternalResource("user.yml");
                    if (config instanceof UnknownResource) {
                        config = ((UnknownResource) config).createFile();
                    }
                    if (config instanceof WritableResourceFile) {
                        Section.saveYaml(facetConfig, (WritableResourceFile) config);
                    }
                    Toolkit.info("Terminating JavaFX thread...");
                    Platform.exit();
                    Toolkit.info("JavaFX thread terminated!");
                    TaskThread thread = getTaskManager().getBackgroundThread();
                    if (thread instanceof TaskThreadImpl) {
                        Toolkit.info("Terminating background thread pool...");
                        ((TaskThreadImpl) thread).shutdown();
                        Toolkit.info("Background thread pool terminated!");
                    }
                    Toolkit.info("Good bye!");
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                System.exit(0);
            }
        }.start();
    }

    @Override
    public Section getConfiguration(ProjectFacet facet) {
        if (facet != null) {
            if (facetConfig.has(facet.getName())) {
                return facetConfig.getMap(facet.getName());
            } else {
                Section map = Section.newMap();
                facetConfig.set(map, facet.getName());
                return map;
            }
        }
        if (facetConfig.has("application")) {
            return facetConfig.getMap("application");
        } else {
            Section map = Section.newMap();
            facetConfig.set(map, "application");
            return map;
        }
    }
}
