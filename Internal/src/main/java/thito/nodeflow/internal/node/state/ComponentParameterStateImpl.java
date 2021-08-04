package thito.nodeflow.internal.node.state;

import javafx.beans.property.*;
import thito.nodeflow.api.config.*;
import thito.nodeflow.api.editor.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.node.state.*;
import thito.nodeflow.internal.editor.*;
import thito.nodeflow.internal.node.*;

import java.lang.reflect.*;
import java.util.*;

public class ComponentParameterStateImpl implements ComponentParameterState {
    private UUID id;
    private ObjectProperty<Object> constantValue = new SimpleObjectProperty<>();
    private Section extras;

    private StandardNodeModule module;

    {
        constantValue.addListener((obs, old, val) -> {
            attemptSave();
            if (module != null) {
                NodeFileSession session = module.getSession();
                if (session != null) {
                    EditorAction.store(module.getSession(), I18n.$("action-parameter-set-value"), () -> {
                        constantValue.set(old);
                    }, () -> {
                        constantValue.set(val);
                    });
                }
            }
        });
    }

    public void attemptSave() {
        if (module != null) {
            NodeFileSession session = module.getSession();
            if (session != null) {
                session.save();
            }
        }
    }

    public ComponentParameterStateImpl(StandardNodeModule module, Section deserialize) {
        this.module = module;
        deserialize(deserialize);
    }

    public ComponentParameterStateImpl(StandardNodeModule module, UUID id, Object constantValue) {
        this.module = module;
        this.id = id;
        this.constantValue.set(constantValue);
        this.extras = Section.newMap();
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public UUID getID() {
        return id;
    }

    @Override
    public Object getConstantValue() {
        return constantValue.getValue();
    }

    @Override
    public void setConstantValue(Object value) {
        constantValue.set(value);
    }

    @Override
    public Section getExtras() {
        return extras;
    }

    @Override
    public Section serialize() {
        Section section = Section.newMap();
        section.set(id.toString(), "id");
        if (ModuleManagerImpl.isSerializable(constantValue.get())) {
            Object val = constantValue.get();
            if (val instanceof Enum) {
                val = ((Enum<?>) val).name();
            }
            if (val instanceof Field) {
                val = ((Field) val).getName();
            }
            if (val instanceof Character) {
                val = (int) ((Character) val).charValue();
            }
            section.set(val, "value");
        }
        section.set(extras, "extras");
        return section;
    }

    @Override
    public void deserialize(Section section) {
        id = UUID.fromString(section.getString("id"));
        constantValue.set(section.getObject("value"));
        if (section.has("extras")) {
            extras = section.getMap("extras");
        } else {
            section.set(extras = Section.newMap(), "extras");
        }
    }
}
