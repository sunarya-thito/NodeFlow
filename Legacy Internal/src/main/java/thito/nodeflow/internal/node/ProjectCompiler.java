package thito.nodeflow.internal.node;

import nodeflow.*;
import thito.nodeflow.api.*;
import thito.nodeflow.api.editor.*;
import thito.nodeflow.api.node.*;
import thito.nodeflow.api.project.*;
import thito.nodeflow.api.resource.*;
import thito.nodeflow.api.task.*;
import thito.nodeflow.internal.editor.config.*;
import thito.nodeflow.internal.editor.record.*;
import thito.nodeflow.internal.node.headless.*;
import thito.nodeflow.internal.project.*;
import thito.reflectedbytecode.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.jar.*;
import java.util.zip.*;

public class ProjectCompiler {
    private Context context;
    private Project project;
    private Set<NodeCompiler> compiledModules = new HashSet<>();
    private WritableResourceFile output;
    private FacetCompilerHandlerImpl handler;
    private int classId, methodId;
    private String packageName = "nodeflow_"+UUID.randomUUID().toString().replace("-", "");
    private ConfigFileCompiler configFileCompiler;
    private RecordFileCompiler recordFileCompiler;

    public ProjectCompiler(Project project, WritableResourceFile output) {
        this.project = project;
        this.output = output;
    }

    public FacetCompilerHandlerImpl getHandler() {
        return handler;
    }

    public Context getContext() {
        return context;
    }

    public FutureSupplier<Boolean> exportProject() {
        CompletableFutureSupplier<Boolean> future = FutureSupplier.createCompletable();
        Task.runOnBackground("pre-compile", () -> {
            Toolkit.log("Compiling project ("+project.getProperties().getName()+")...");
            try {
                Context.getContext().close();
            } catch (Throwable t) {
            }
            context = Context.open();
            handler = new FacetCompilerHandlerImpl(this, context, project);
            try {
                findFiles();
                findAllRecords();
                findAllConfigs();
            } catch (Throwable t) {
                t.printStackTrace();
            }
            FacetCompilerSession session = project.getFacet().createCompiler();
            session.linkedPreCompileTask(handler, future, () -> {
                Task.runOnBackground("compile", () -> {
                    if (future.isComplete()) return;
                    Toolkit.log("Compiling modules...");
                    try {
                        findAllModules();
                        session.linkedPostCompileTask(handler, future, () -> {
                            Task.runOnBackground("post-compile", () -> {
                                if (future.isComplete()) return;
                                Toolkit.log("Post compile...");
                                try {
                                    export();
                                    context.close();
                                    future.complete(true);
                                } catch (Throwable t) {
                                    future.error(t);
                                }
                                Toolkit.log("Done!");
                            });
                        });
                    } catch (Throwable t) {
                        future.error(t);
                    }
                });
            });
        });
        return future;
    }

    public RecordFileCompiler getRecordFileCompiler() {
        return recordFileCompiler;
    }

    public ConfigFileCompiler getConfigFileCompiler() {
        return configFileCompiler;
    }

    public Set<NodeCompiler> getCompiledModules() {
        return compiledModules;
    }

    private void findAllModules() throws IOException {
        bulkFind(project.getSourceDirectory());
    }

    private void findAllConfigs() throws IOException {
        configFileCompiler = new ConfigFileCompiler(this);
        bulkFindConfig(project.getSourceDirectory());
    }

    private void findAllRecords() throws IOException {
        recordFileCompiler = new RecordFileCompiler(context, getPackageName());
        bulkFindRecord(project.getSourceDirectory());
    }

    private void findFiles() throws IOException {
        bulkFiles(project.getSourceDirectory());
    }

    private Set<ResourceFile> exportedFiles = new HashSet<>();
    private void bulkFiles(ResourceDirectory directory) throws IOException {
        for (Resource resource : directory.getChildren()) {
            if (resource instanceof ResourceDirectory) {
                bulkFiles((ResourceDirectory) resource);
            } else if (resource instanceof ResourceFile) {
                EditorManager editorManager = NodeFlow.getApplication().getEditorManager();
                FileHandler handler = editorManager.getRegisteredHandler(((ResourceFile) resource).getExtension());
                if (handler != null && handler.exportFile((ResourceFile) resource)) {
                    exportedFiles.add((ResourceFile) resource);
                }
            }
        }
    }

    private void bulkFindConfig(ResourceDirectory directory) throws IOException {
        for (Resource resource : directory.getChildren()) {
            if (resource instanceof ResourceDirectory) {
                bulkFindConfig((ResourceDirectory) resource);
            } else if (resource instanceof ResourceFile) {
                if (((ResourceFile) resource).getExtension().equals("cfg")) {
                    loadConfig((ResourceFile) resource);
                }
            }
        }
    }

    private void loadConfig(ResourceFile file) throws IOException {
        Toolkit.log("Compiling field map "+file.getName()+"...");
        String relativePath = new File(project.getSourceDirectory().getPath()).toPath().relativize(new File(file.getPath()).toPath()).toString();
        int index = relativePath.lastIndexOf('.');
        if (index >= 0) {
            relativePath = relativePath.substring(0, index);
        }
        ConfigFileModule module = new ConfigFileModule(relativePath);
        try (InputStream inputStream = file.openInput()) {
            module.load(inputStream);
        }
        configFileCompiler.compile(module);
    }

    private void bulkFindRecord(ResourceDirectory directory) throws IOException {
        for (Resource resource : directory.getChildren()) {
            if (resource instanceof ResourceDirectory) {
                bulkFindRecord((ResourceDirectory) resource);
            } else if (resource instanceof ResourceFile) {
                if (((ResourceFile) resource).getExtension().equals("rec")) {
                    loadRecord((ResourceFile) resource);
                }
            }
        }
    }

    private void loadRecord(ResourceFile file) throws IOException {
        Toolkit.log("Compiling record "+file.getName()+"...");
        RecordFileModule module = new RecordFileModule(file.getName(), file, false);
        try (InputStream inputStream = file.openInput()) {
            module.load(inputStream);
        }
        recordFileCompiler.compile(module);
    }

    private void bulkFind(ResourceDirectory directory) throws IOException {
        for (Resource resource : directory.getChildren()) {
            if (resource instanceof ResourceDirectory) {
                bulkFind((ResourceDirectory) resource);
            } else if (resource instanceof ResourceFile) {
                if (((ResourceFile) resource).getExtension().equals("ndx")) {
                    loadAndCompile((ResourceFile) resource);
                }
            }
        }
    }

    public String getPackageName() {
        return packageName;
    }

    private void loadAndCompile(ResourceFile moduleFile) throws IOException {
        Toolkit.log("Compiling "+moduleFile.getName()+"...");
        int id = 0;
        NodeModule module = new HeadlessNodeModule(id, moduleFile.getName(), moduleFile);
        try (InputStream inputStream = moduleFile.openInput()) {
            ModuleManagerImpl.getInstance().loadHeadlessModule(module, inputStream);
        }
        compileModule(module);
    }

    private void compileModule(NodeModule module) {
        NodeCompiler compiler = new NodeCompiler(context, this, module);
        Toolkit.log("Compiled "+module.getName()+"!");
        compiledModules.add(compiler);
    }

    private void export() throws IOException {
        Toolkit.log("Exporting to "+output.getPath()+"...");
        try (JarOutputStream jarOutputStream = new JarOutputStream(output.openOutput())) {
            jarOutputStream.setLevel(JarOutputStream.DEFLATED);
            jarOutputStream.setComment("Generated using NodeFlow "+NodeFlow.getApplication().getVersion());
            Class<?>[] BUNDLED = {Utility.class, Record.class, Configuration.class};
            for (Class<?> bundled : BUNDLED) {
                jarOutputStream.putNextEntry(new ZipEntry(bundled.getName().replace('.', '/')+".class"));
                try (InputStream inputStream = bundled.getResourceAsStream(bundled.getSimpleName()+".class")) {
                    byte[] buff = new byte[1024 * 8];
                    int len;
                    while ((len = inputStream.read(buff, 0, buff.length)) != -1) {
                        jarOutputStream.write(buff, 0, len);
                    }
                }
                jarOutputStream.closeEntry();
            }
            for (GClass clazz : context.getClasses()) {
                try {
                    jarOutputStream.putNextEntry(new ZipEntry(clazz.getName().replace('.', '/') + ".class"));
                } catch (Throwable t) {
                    t.printStackTrace();
                    continue;
                }
                jarOutputStream.write(context.writeClass(clazz));
                jarOutputStream.closeEntry();
            }
            try {
                jarOutputStream.putNextEntry(new ZipEntry("META-INF/MANIFEST.MF"));
                StringWriter writer = new StringWriter();
                handler.getManifest().forEach((key, value) -> {
                    writer.write(key + ": " + value);
                    writer.write(System.lineSeparator());
                });
                jarOutputStream.write(writer.toString().getBytes(StandardCharsets.UTF_8));
                jarOutputStream.closeEntry();
            } catch (Throwable t) {
                t.printStackTrace();
            }
            for (Map.Entry<String, InputStream> entry : handler.getBundleMap().entrySet()) {
                if (entry.getValue() == null) {
                    System.out.println("Bundle with name "+entry.getKey()+" is null");
                    continue;
                }
                if (entry.getKey().equalsIgnoreCase("META-INF/MANIFEST.MF")) continue;
                try (InputStream inputStream = entry.getValue()) {
                    try {
                        jarOutputStream.putNextEntry(new ZipEntry(entry.getKey()));
                    } catch (Throwable t) {
                        t.printStackTrace();
                        continue;
                    }
                    byte[] buff = new byte[1024 * 8];
                    int len;
                    while ((len = inputStream.read(buff, 0, buff.length)) != -1) {
                        jarOutputStream.write(buff, 0, len);
                    }
                    jarOutputStream.closeEntry();
                }
            }
            for (ResourceFile exported : exportedFiles) {
                Path path = new File(exported.getPath()).toPath();
                String rel = new File(project.getSourceDirectory().getPath()).toPath().relativize(path).toString();
                try {
                    jarOutputStream.putNextEntry(new ZipEntry(rel));
                } catch (Throwable t) {
                    t.printStackTrace();
                    continue;
                }
                try (InputStream inputStream = exported.openInput()) {
                    byte[] buff = new byte[1024 * 8];
                    int len;
                    while ((len = inputStream.read(buff, 0, buff.length)) != -1) {
                        jarOutputStream.write(buff, 0, len);
                    }
                }
                jarOutputStream.closeEntry();
            }
        }
    }

    public int requestClassId() {
        return classId++;
    }

    public int requestMethodId() {
        return methodId++;
    }
}
