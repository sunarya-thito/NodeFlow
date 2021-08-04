package thito.nodeflow.internal.node.headless.state;

import thito.nodeflow.api.config.*;
import thito.nodeflow.api.node.state.*;
import thito.nodeflow.internal.node.*;
import thito.nodeflow.internal.node.headless.*;

import java.lang.reflect.*;
import java.util.*;

public class HeadlessComponentParameterStateImpl implements ComponentParameterState {
    private UUID id;
    private Object constantValue;

    private HeadlessNodeModule module;
    private Section extras;

    public HeadlessComponentParameterStateImpl(HeadlessNodeModule module, Section deserialize) {
        this.module = module;
        deserialize(deserialize);
    }

    public HeadlessComponentParameterStateImpl(HeadlessNodeModule module, UUID id, Object constantValue) {
        this.module = module;
        this.id = id;
        this.constantValue = constantValue;
        this.extras = Section.newMap();
    }

    @Override
    public UUID getID() {
        return id;
    }

    @Override
    public Object getConstantValue() {
        return constantValue;
    }

    @Override
    public void setConstantValue(Object value) {
        constantValue = value;
    }

    @Override
    public Section serialize() {
        Section section = Section.newMap();
        section.set(id.toString(), "id");
        if (ModuleManagerImpl.isSerializable(constantValue)) {
            Object val = constantValue;
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
        constantValue = section.getObject("value");
        if (section.has("extras")) {
            extras = section.getMap("extras");
        } else {
            section.set(extras = Section.newMap(), "extras");
        }
    }

    @Override
    public Section getExtras() {
        return null;
    }
}
