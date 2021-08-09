package thito.nodeflow.internal.node.headless.state;

import javafx.beans.property.*;
import thito.nodeflow.api.config.*;
import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.node.state.*;
import thito.nodeflow.internal.node.*;
import thito.nodeflow.internal.node.headless.*;
import thito.nodeflow.internal.node.provider.*;

import java.util.*;

public class HeadlessComponentStateImpl implements ComponentState {
    private String providerId;
    private NodeProvider provider;

    private ComponentParameterState[] parameters;

    private double x;
    private double y;

    private String name;
    private UUID id;
    private HeadlessNodeModule module;
    private Section extras;
    public HeadlessComponentStateImpl(HeadlessNodeModule module) {
        this.module = module;
    }

    public HeadlessComponentStateImpl(HeadlessNodeModule module, String providerId) {
        this.module = module;
        this.providerId = providerId;
        id = UUID.randomUUID();
    }

    @Override
    public ObjectProperty<NodeTag> getTag() {
        ObjectProperty<NodeTag> tag = new SimpleObjectProperty<>();
        try {
            tag.set(NodeTag.valueOf(extras.getString("tag")));
        } catch (Throwable t) {
        }
        return tag;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Section getExtras() {
        if (extras == null) extras = Section.newMap();
        return extras;
    }

    @Override
    public String getProviderID() {
        return providerId;
    }

    @Override
    public void setProviderID(String id) {
        this.providerId = id;
        provider = null;
    }

    @Override
    public UUID getID() {
        return id;
    }

    public void setProvider(NodeProvider provider) {
        this.provider = provider;
    }

    @Override
    public NodeProvider getProvider() {
        if (provider == null) {
            provider = ModuleManagerImpl.getInstance().getProvider(providerId);
            if (provider == null) throw new IllegalStateException("PROVIDER NOT FOUND: "+providerId);
            updateParameters(((AbstractNodeProvider) provider).getParameters().stream().map(x -> new HeadlessComponentParameterStateImpl(module, UUID.randomUUID(), null)).toArray(ComponentParameterState[]::new));
        }
        return provider;
    }

    public void updateParameters(ComponentParameterState[] parameters) {
        if (this.parameters != null) {
            if (this.parameters.length >= parameters.length) {
                int len;
                if ((len = this.parameters.length) != parameters.length) {
                    this.parameters = Arrays.copyOf(this.parameters, parameters.length);
                }
                for (int i = len; i < parameters.length; i++) {
                    this.parameters[i].setConstantValue(parameters[i].getConstantValue());
                }
            } else if (this.parameters.length < parameters.length) {
                int from = this.parameters.length;
                this.parameters = Arrays.copyOf(this.parameters, parameters.length);
                for (; from < parameters.length; from++) {
                    this.parameters[from] = parameters[from];
                }
            }
        } else {
            this.parameters = parameters;
        }
    }

    public void setParameters(ComponentParameterState[] parameters) {
        this.parameters = parameters;
    }

    @Override
    public ComponentParameterState[] getParameters() {
        return parameters;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public void setX(double x) {
        this.x = x;
    }

    @Override
    public void setY(double y) {
        this.y = y;
    }

    @Override
    public Section serialize() {
        Section section = Section.newMap();
        section.set(name, "name");
        section.set(x, "x");
        section.set(y, "y");
        section.set(providerId, "providerId");
        section.set(id.toString(), "id");
        ListSection params = Section.newList();
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i] == null) {
                params.add(null);
                continue;
            }
            Section param = parameters[i].serialize();
            params.add(param);
        }
        section.set(params, "parameters");
        section.set(getExtras(), "extras");
        return section;
    }

    @Override
    public void deserialize(Section section) {
        name = section.getString("name");
        x = section.getDouble("x");
        y = section.getDouble("y");
        id = UUID.fromString(section.getString("id"));
        providerId = section.getString("providerId");
        ListSection params = section.getList("parameters");
        parameters = new ComponentParameterState[params.size()];
        extras = section.has("extras") ? section.getMap("extras") : Section.newMap();
        for (int i = 0; i < params.size(); i++) {
            Section param = params.getMap(i);
            if (param == null) {
                parameters[i] = null;
            } else {
                parameters[i] = new HeadlessComponentParameterStateImpl(module, params.getMap(i));
            }
        }
    }
}
