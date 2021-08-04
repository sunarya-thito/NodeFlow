package thito.nodeflow.internal.editor.record;

import nodeflow.*;
import thito.reflectedbytecode.*;

import java.lang.reflect.*;
import java.util.*;

public class RecordFileCompiler {
    private Map<String, GClass> compiled = new HashMap<>();
    private Map<UUID, GField> compiledField = new HashMap<>();
    private Map<String, GConstructor> compiledDefaultConstructor = new HashMap<>();
    private Map<String, GConstructor> compiledConstructor = new HashMap<>();
    private Context context;

    public Map<String, GConstructor> getCompiledConstructor() {
        return compiledConstructor;
    }

    public Map<String, GConstructor> getCompiledDefaultConstructor() {
        return compiledDefaultConstructor;
    }

    private String packName;

    public RecordFileCompiler(Context context, String packName) {
        this.context = context;
        this.packName = packName;
    }

    public Map<String, GClass> getCompiled() {
        return compiled;
    }

    public Map<UUID, GField> getCompiledField() {
        return compiledField;
    }

    private int id = 0;
    public void compile(RecordFileModule module) {
        GClass clazz = context.createClass(packName+".Record_"+id++);
        clazz.thatImplements(Record.class);
        compiled.put(module.getFile().getPath(), clazz);
        GConstructor a = clazz.declareConstructor();
        compiledDefaultConstructor.put(module.getFile().getPath(), a);
        GConstructor cons = clazz.declareConstructor();
        compiledConstructor.put(module.getFile().getPath(), cons);
        cons.parameters(module.getItems().stream().map(RecordItem::getType).toArray(Type[]::new));
        Map<RecordItem, GField> fieldMap = new HashMap<>();
        for (RecordItem item : module.getItems()) {
            GField field = clazz.declareField("field_"+item.getId().toString().replace("-", "_"), item.getType());
            field.modifier().makePublic();
            fieldMap.put(item, field);
            compiledField.put(item.getId(), field);
        }
        cons.body(body -> {
            for (int i = 0; i < module.getItems().size(); i++) {
                fieldMap.get(module.getItems().get(i)).set(body, body.getArgument(i + 1));
            }
        });
    }
}
