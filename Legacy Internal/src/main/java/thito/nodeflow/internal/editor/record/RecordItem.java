package thito.nodeflow.internal.editor.record;

import java.util.*;

public class RecordItem {
    private UUID id;
    private String name;
    private Class<?> type;
    private RecordFileModule module;

    public RecordItem(RecordFileModule module, UUID id, String name, Class<?> type) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.module = module;
    }

    public RecordFileModule getModule() {
        return module;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
        module.attemptSave();
    }

    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecordItem that = (RecordItem) o;
        return id.equals(that.id);
    }
}
