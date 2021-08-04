package thito.nodeflow.internal.node.state;

import javafx.beans.property.*;
import thito.nodeflow.api.config.*;
import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.node.state.*;
import thito.nodeflow.internal.node.*;
import thito.nodeflow.internal.node.provider.*;

import java.io.*;
import java.util.*;

public class ComponentStateImpl implements ComponentState, Serializable {
    private String providerId;
    private NodeProvider provider;

    private ComponentParameterState[] parameters;

    private double x;
    private double y;
    private String name;

    private Section extras;

    private UUID id;
    private StandardNodeModule module;
    private ObjectProperty<NodeTag> tags = new SimpleObjectProperty<>(NodeTag.NORMAL);

    private boolean deserializing = false;

    {
        tags.addListener(observable -> {
            if (deserializing) return;
            attemptSave();
        });
    }

    public ComponentStateImpl() {
    }

    public ComponentStateImpl(StandardNodeModule module) {
        this.module = module;
    }

    public ComponentStateImpl(StandardNodeModule module, String providerId) {
        this.module = module;
        this.providerId = providerId;
        id = UUID.randomUUID();
        this.extras = Section.newMap();
    }

    public void setModule(StandardNodeModule module) {
        this.module = module;
    }

    private void writeObject(ObjectOutputStream outputStream) throws IOException {
        StringWriter writer = new StringWriter();
        Section.saveYaml(serialize(), writer);
        outputStream.writeUTF(writer.toString());
    }

    private void readObject(ObjectInputStream inputStream) throws IOException {
        tags = new SimpleObjectProperty<>(NodeTag.NORMAL);
        deserialize(Section.loadYaml(new StringReader(inputStream.readUTF())));
    }

    public void randomizeID() {
        id = UUID.randomUUID();
        for (ComponentParameterState state : parameters) {
            ((ComponentParameterStateImpl) state).setId(UUID.randomUUID());
        }
    }

    @Override
    public ObjectProperty<NodeTag> getTag() {
        return tags;
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
        attemptSave();
    }

    public void attemptSave() {
        if (module != null) module.attemptSave();
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
            if (!(provider instanceof UnknownProvider)) {
                updateParameters(((AbstractNodeProvider) provider).getParameters().stream().map(x -> new ComponentParameterStateImpl(module, UUID.randomUUID(), null)).toArray(ComponentParameterState[]::new));
            }
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
        attemptSave();
    }

    public void setParameters(ComponentParameterState[] parameters) {
        this.parameters = parameters;
        attemptSave();
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
        attemptSave();
    }

    @Override
    public void setY(double y) {
        this.y = y;
        attemptSave();
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
        if (tags.get() != null) {
            getExtras().set(tags.get().name(), "tag");
        }
        section.set(getExtras(), "extras");
        return section;
    }

    @Override
    public void deserialize(Section section) {
        deserializing = true;
        name = section.getString("name");
        x = section.getDouble("x");
        y = section.getDouble("y");
        id = UUID.fromString(section.getString("id"));
        providerId = section.getString("providerId");
        ListSection params = section.getList("parameters");
        parameters = new ComponentParameterState[params.size()];
        for (int i = 0; i < params.size(); i++) {
            Section param = params.getMap(i);
            if (param == null) {
                parameters[i] = null;
            } else {
                parameters[i] = new ComponentParameterStateImpl(module, params.getMap(i));
            }
        }
        if (section.has("extras")) {
            extras = section.getMap("extras");
        } else {
            section.set(extras = Section.newMap(), "extras");
        }
        try {
            tags.set(NodeTag.valueOf(extras.getString("tag")));
        } catch (Throwable t) {
            tags.set(NodeTag.NORMAL);
            t.printStackTrace();
        }
        deserializing = false;
    }
}
