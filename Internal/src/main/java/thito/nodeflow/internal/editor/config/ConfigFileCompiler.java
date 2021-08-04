package thito.nodeflow.internal.editor.config;

import nodeflow.*;
import thito.nodeflow.internal.editor.config.savemodes.*;
import thito.nodeflow.internal.node.*;
import thito.reflectedbytecode.*;
import thito.reflectedbytecode.jvm.*;

import java.io.*;
import java.util.*;

public class ConfigFileCompiler {
    private Map<UUID, GField> fieldMap = new HashMap<>();
    private List<GClass> compiled = new ArrayList<>();
    private Map<ConfigFileModule, GMethod> saveMethod = new HashMap<>();
    private Map<ConfigFileModule, GMethod> loadMethod = new HashMap<>();
    private int id = 0;
    private ProjectCompiler compiler;

    public ConfigFileCompiler(ProjectCompiler compiler) {
        this.compiler = compiler;
    }

    public List<GClass> getCompiled() {
        return compiled;
    }

    public boolean hasField(UUID id) {
        return fieldMap.containsKey(id);
    }

    public Map<ConfigFileModule, GMethod> getSaveMethod() {
        return saveMethod;
    }

    public Map<ConfigFileModule, GMethod> getLoadMethod() {
        return loadMethod;
    }

    public void compile(ConfigFileModule module) {
        GClass classes = compiler.getContext().createClass(compiler.getPackageName()+".Config_"+id++);
        classes.thatImplements(Configuration.class);
        Map<GField, String> fields = new HashMap<>(module.getValues().size());
        for (ConfigFileModule.Value value : module.getValues()) {
            ConfigValueType type = ConfigFileManager.getManager().getType(value.getType());
            GField field = classes.declareField("field_"+value.getID().toString().replace("-", "_"), type.getFieldType());
            field.modifier().makeStatic().makePublic();
            field.initialValue(value.getConstantValue());
            fieldMap.put(value.getID(), field);
            fields.put(field, value.getName());
        }
        if (module.isAllowSave() && !(module.getMode() instanceof DisabledSaveMode)) {
            GMethod save = classes.declareMethod("save").parameters(OutputStream.class).modifier().makeStatic().done().body(body -> {
                module.getMode().handleSaveMethod(this, body.getArgument(0), body, fields);
            });
            saveMethod.put(module, save);
        }
        if (module.isAllowLoad() && !(module.getMode() instanceof DisabledSaveMode)) {
            GMethod load = classes.declareMethod("load").parameters(InputStream.class).modifier().makeStatic().done().body(body -> {
                module.getMode().handleLoadMethod(this, body.getArgument(0), body, fields);
            });
            loadMethod.put(module, load);
        }
        compiled.add(classes);
    }

    public Reference getReference(UUID id) {
        GField field = fieldMap.get(id);
        return field == null ? Java.Null() : field.get(null);
    }

    public void setReference(UUID id, Reference reference) {
        GField field = fieldMap.get(id);
        if (field != null) {
            field.set(null, reference);
        }
    }

}
