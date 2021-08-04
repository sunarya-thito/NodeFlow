package thito.nodeflow.internal.project;

import thito.nodeflow.api.*;
import thito.nodeflow.api.node.*;
import thito.nodeflow.api.node.eventbus.*;
import thito.nodeflow.api.node.eventbus.command.*;
import thito.nodeflow.api.project.*;
import thito.nodeflow.api.resource.*;
import thito.nodeflow.internal.node.*;
import thito.reflectedbytecode.*;
import thito.reflectedbytecode.jvm.*;

import java.io.*;
import java.util.*;
import java.util.jar.*;
import java.util.stream.*;

public class FacetCompilerHandlerImpl implements FacetCompilerHandler {
    private List<CompiledEvent> compiledEvents = new ArrayList<>();
    private List<CompiledCommandNode> compiledCommands = new ArrayList<>();
    private Context context;
    private Map<String, InputStream> bundleMap = new HashMap<>();
    private Project project;
    private Properties manifest = new Properties();
    private ProjectCompiler pc;

    public FacetCompilerHandlerImpl(ProjectCompiler compiler, Context context, Project project) {
        this.context = context;
        this.project = project;
        this.pc = compiler;

        // initialize manifest using default values
        manifest.setProperty("Manifest-Version", "1.0");
        manifest.setProperty("Built-By", System.getProperty("user.name"));
        manifest.setProperty("Created-By", "NodeFlow "+ NodeFlow.getApplication().getVersion());
        manifest.setProperty("Archiver-Version", "Plexus Archiver");
        manifest.setProperty("Build-Jdk", System.getProperty("java.version"));
    }

    public List<CompiledCommandNode> getCompiledCommands() {
        return compiledCommands;
    }

    @Override
    public List<CompiledCommandNode> getCommandMethods(CommandNodeCategory category) {
        List<CompiledCommandNode> commands = new ArrayList<>();
        for (CompiledCommandNode cmd : compiledCommands) {
            if (cmd.getNode().getState().getProvider().getCategory() == category) {
                commands.add(cmd);
            }
        }
        return commands;
    }

    @Override
    public String getPackageName() {
        return pc.getPackageName();
    }

    @Override
    public Set<NodeModule> getModules() {
        return pc.getCompiledModules().stream().map(NodeCompiler::getModule).collect(Collectors.toSet());
    }

    @Override
    public Properties getManifest() {
        return manifest;
    }

    @Override
    public List<CompiledEvent> getEventMethods(EventProvider provider) {
        List<CompiledEvent> events = new ArrayList<>();
        for (int i = 0; i < compiledEvents.size(); i++) {
            CompiledEvent compiledEvent = compiledEvents.get(i);
            if (compiledEvent.getProvider() == provider) {
                events.add(compiledEvent);
            }
        }
        events.sort(Comparator.comparingInt(x -> x.getNode().getPriority().ordinal()));
        return events;
    }

    public List<CompiledEvent> getCompiledEvents() {
        return compiledEvents;
    }

    public Map<String, InputStream> getBundleMap() {
        return bundleMap;
    }


    @Override
    public Project getProject() {
        return project;
    }

    @Override
    public void configLoad(Reference configDirectory) {
        pc.getConfigFileCompiler().getLoadMethod().forEach((module, method) -> {
            ILocalField file = Code.getCode().getLocalFieldMap().createField(Java.Class(File.class));
            file.set(Java.Class(File.class).getConstructor(File.class, String.class).get()
                    .newInstance(configDirectory, module.getName()));
            Java.If(file.get().method("exists").invoke()).isTrue().Then(() -> {
                Java.Try(() -> {
                    ILocalField inputStream = Code.getCode().getLocalFieldMap().createField(Java.Class(InputStream.class));
                    inputStream.set(Java.Class(FileInputStream.class).getConstructor(File.class).get()
                            .newInstance(file.get()));
                    method.invoke(null, inputStream.get());
                    inputStream.get().method("close").invoke();
                }).Catch(Throwable.class).Caught(error -> {
                    error.printStackTrace();
                });
            }).EndIf();
        });
    }

    @Override
    public void configSave(Reference configDirectory) {
        configDirectory.method("mkdirs").invoke();
        pc.getConfigFileCompiler().getSaveMethod().forEach((module, method) -> {
            Java.Try(() -> {
                ILocalField file = Code.getCode().getLocalFieldMap().createField(Java.Class(File.class));
                file.set(Java.Class(File.class).getConstructor(File.class, String.class).get()
                        .newInstance(configDirectory, module.getName()));
                ILocalField outputStream = Code.getCode().getLocalFieldMap().createField(Java.Class(OutputStream.class));
                outputStream.set(Java.Class(FileOutputStream.class).getConstructor(File.class).get()
                        .newInstance(file.get()));
                method.invoke(null, outputStream.get());
                outputStream.get().method("close").invoke();
            }).Catch(Throwable.class).Caught(error -> {
                error.printStackTrace();
            });
        });
    }

    @Override
    public List<CompiledEvent> getEventMethods(String providerId) {
        List<CompiledEvent> events = new ArrayList<>();
        for (int i = 0; i < compiledEvents.size(); i++) {
            CompiledEvent compiledEvent = compiledEvents.get(i);
            if (compiledEvent.getProvider().getID().equals(providerId)) {
                events.add(compiledEvent);
            }
        }
        events.sort(Comparator.comparingInt(x -> x.getNode().getPriority().ordinal()));
        return events;
    }

    @Override
    public Context getCompilerContext() {
        return context;
    }

    @Override
    public void bundleResource(String name, InputStream inputStream) {
        bundleMap.put(name, inputStream);
    }

    @Override
    public void bundleResource(String name, byte[] bytes) {
        bundleResource(name, new ByteArrayInputStream(bytes));
    }

    @Override
    public void bundleResource(String name, JarFile file) {
        file.stream().forEach(entry -> {
            try {
                bundleResource(entry.getName(), file.getInputStream(entry));
            } catch (IOException e) {
            }
        });
    }

    @Override
    public void bundleResource(String name, Resource resource) {
        if (resource instanceof ResourceFile) {
            bundleResource(name, ((ResourceFile) resource).openInput());
        }
    }
}
