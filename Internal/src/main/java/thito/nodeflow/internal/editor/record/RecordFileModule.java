package thito.nodeflow.internal.editor.record;

import javafx.beans.*;
import javafx.collections.*;
import nodeflow.*;
import thito.nodeflow.api.*;
import thito.nodeflow.api.config.*;
import thito.nodeflow.api.resource.*;
import thito.reflectedbytecode.*;

import java.io.*;
import java.util.*;

public class RecordFileModule {
    private RecordFileSession session;
    private ObservableList<RecordItem> items = FXCollections.observableArrayList();
    private Class<?> generated; // does not contains attributes/fields/methods

    private String name;
    private ResourceFile file;
    public RecordFileModule(String name, ResourceFile file, boolean generateClass) {
        this.name = name;
        this.file = file;
        items.addListener((InvalidationListener) obs -> attemptSave());
        if (generateClass) {
            try (Context context = Context.open()) {
                GClass clazz = context.createClass(name);
                clazz.thatImplements(Record.class);
                generated = context.loadIntoMemory(getClass().getClassLoader()).loadClass(name);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public ResourceFile getFile() {
        return file;
    }

    public String getName() {
        return name;
    }

    public Class<?> getGenerated() {
        return generated;
    }

    public RecordFileSession getSession() {
        return session;
    }

    public void attemptSave() {
        if (session != null) {
            session.save();
        }
    }

    public void setSession(RecordFileSession session) {
        this.session = session;
    }

    public void load(InputStream inputStream) {
        InputStreamReader reader = new InputStreamReader(inputStream);
        Section section = Section.loadYaml(reader);
        if (section == null) return;
        ListSection list = section.getList("fields");
        for (int i = 0; i < list.size(); i++) {
            MapSection map = list.getMap(i);
            try {
                UUID id = UUID.fromString(map.getString("id"));
                String name = map.getString("name");
                Class<?> type = NodeFlow.getApplication().getBundleManager().findClass(map.getString("type"));
                if (name != null && type != null) {
                    RecordItem item = new RecordItem(this, id, name, type);
                    items.add(item);
                }
            } catch (IllegalArgumentException e) {
            }
        }
    }

    public void save(OutputStream outputStream) {
        OutputStreamWriter writer = new OutputStreamWriter(outputStream);
        Section section = Section.newMap();
        ListSection list = Section.newList();
        for (RecordItem item : items) {
            MapSection map = Section.newMap();
            map.set(item.getId().toString(), "id");
            map.set(item.getName(), "name");
            map.set(item.getType().getName(), "type");
            list.add(map);
        }
        section.set(list, "fields");
        Section.saveYaml(section, writer);
    }

    public ObservableList<RecordItem> getItems() {
        return items;
    }
}
