package thito.nodeflow.internal.editor.config;

import javafx.beans.*;
import javafx.collections.*;
import thito.nodeflow.api.config.*;
import thito.nodeflow.api.locale.*;

import java.io.*;
import java.util.*;

public class ConfigFileModule {
    private ConfigFileSaveMode mode = ConfigFileManager.getManager().getSaveMode(null);
    private ObservableList<Value> values = FXCollections.observableArrayList();

    private boolean allowSave, allowLoad;

    public ObservableList<Value> getValues() {
        return values;
    }

    private String name;

    public ConfigFileModule(String name) {
        this.name = name;
        values.addListener((InvalidationListener) observable -> attemptSave());
    }

    public String getName() {
        String ext = getMode().getExtension();
        return ext == null ? name : name+"."+ext;
    }

    private ConfigFileSession session;

    public void setSession(ConfigFileSession session) {
        this.session = session;
    }

    public ConfigFileSession getSession() {
        return session;
    }

    public ConfigFileSaveMode getMode() {
        return mode;
    }

    public void setMode(ConfigFileSaveMode mode) {
        this.mode = mode;
        attemptSave();
    }

    public boolean isAllowLoad() {
        return allowLoad;
    }

    public boolean isAllowSave() {
        return allowSave;
    }

    public void setAllowLoad(boolean allowLoad) {
        this.allowLoad = allowLoad;
        attemptSave();
    }

    public void setAllowSave(boolean allowSave) {
        this.allowSave = allowSave;
        attemptSave();
    }

    public void attemptSave() {
        if (session != null) {
            session.save();
        }
    }

    public void load(InputStream inputStream) {
        InputStreamReader reader = new InputStreamReader(inputStream);
        Section section = Section.loadYaml(reader);
        if (section == null) return;
        mode = ConfigFileManager.getManager().getSaveMode(section.getString("mode"));
        allowSave = section.getBoolean("save");
        allowLoad = section.getBoolean("load");
        ListSection val = section.getList("values");
        Set<String> antiDupe = new HashSet<>();
        for (int i = 0; i < val.size(); i++) {
            Section v = val.getMap(i);
            String uuid = v.getString("uuid");
            String name = v.getString("name");
            String type = v.getString("type");
            Object value = v.getObject("value");
            if (uuid != null && name != null && type != null) {
                if (antiDupe.add(name)) {
                    values.add(new Value(UUID.fromString(uuid), type, name, value));
                }
            }
        }
    }

    public void save(OutputStream outputStream) {
        OutputStreamWriter writer = new OutputStreamWriter(outputStream);
        Section section = Section.newMap();
        ListSection list = Section.newList();
        section.set(mode.getId(), "mode");
        section.set(allowLoad, "load");
        section.set(allowSave, "save");
        for (Value val : values) {
            Section v = Section.newMap();
            v.set(val.name, "name");
            v.set(val.type, "type");
            v.set(val.constantValue, "value");
            v.set(val.uuid.toString(), "uuid");
            list.add(v);
        }
        section.set(list, "values");
        Section.saveYaml(section, writer);
    }

    public class Value {
        private UUID uuid;
        private String type;
        private String name;
        private Object constantValue;

        public Value(UUID uuid, String type, String name, Object constantValue) {
            this.uuid = uuid;
            this.type = type;
            this.name = name;
            this.constantValue = constantValue;
        }

        public UUID getID() {
            return uuid;
        }

        public String getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
            attemptSave();
        }

        public Object getConstantValue() {
            return constantValue;
        }

        public void setConstantValue(Object constantValue) {
            Object old = this.constantValue;
            this.constantValue = constantValue;
            if (session != null) {
                session.getUndoManager().storeAction(I18n.$("action-variable-value-change").stringBinding(), () -> {
                    this.constantValue = old;
                }, () -> {
                    this.constantValue = constantValue;
                });
            }
            attemptSave();
        }

        public ConfigFileModule getModule() {
            return ConfigFileModule.this;
        }
    }
}
