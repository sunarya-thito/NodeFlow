package thito.nodeflow.internal.node;

import javafx.geometry.*;
import net.md_5.bungee.api.chat.*;
import thito.nodeflow.api.*;
import thito.nodeflow.api.config.*;
import thito.nodeflow.api.editor.node.Node;
import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.node.*;
import thito.nodeflow.api.node.eventbus.*;
import thito.nodeflow.api.node.state.*;
import thito.nodeflow.api.project.*;
import thito.nodeflow.api.ui.*;
import thito.nodeflow.internal.node.eventbus.*;
import thito.nodeflow.internal.node.headless.*;
import thito.nodeflow.internal.node.headless.state.*;
import thito.nodeflow.internal.node.parameter.CharacterParameter;
import thito.nodeflow.internal.node.parameter.StringParameter;
import thito.nodeflow.internal.node.parameter.*;
import thito.nodeflow.internal.node.provider.*;
import thito.nodeflow.internal.node.state.*;
import thito.nodejfx.parameter.*;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

public class ModuleManagerImpl implements ModuleManager {

    private static ModuleManagerImpl manager = new ModuleManagerImpl();
    public static ModuleManagerImpl getInstance() {
        return manager;
    }

    @Override
    public void loadModule(NodeModule module, InputStream inputStream, boolean ignoreMissingProvider) throws MissingProviderException {
        Section section = Section.loadYaml(new InputStreamReader(inputStream));
        ModuleEditorState editor = module.getEditorState();
        editor.setOffsetX(section.getDouble("editor", "offsetX"));
        editor.setOffsetY(section.getDouble("editor", "offsetY"));
        editor.setPivotX(section.getDouble("editor", "pivotX"));
        editor.setPivotY(section.getDouble("editor", "pivotY"));
        editor.setZoom(section.getDouble("editor", "zoom"));
        ListSection members = section.getList("groups");
        for (int i = 0; i < members.size(); i++) {
            MapSection member = members.getMap(i);
            GroupState state = new GroupStateImpl((StandardNodeModule) module);
            state.deserialize(member);
            NodeGroupImpl group = new NodeGroupImpl(module, state);
            group.impl_getPeer().dropPointProperty().set(new Point2D(
                    group.impl_getPeer().getLayoutX(),
                    group.impl_getPeer().getLayoutY()
            ));
            group.impl_getPeer().resizeBoundsProperty().set(group.impl_getPeer().getExactBounds());
            ((AbstractNodeModule) module).groups().add(group);
        }
        members = section.getList("nodes");
        Map<UUID, NodeParameter> parameterMap = new HashMap<>();
        for (int i = 0; i < members.size(); i++) {
            try {
                MapSection member = members.getMap(i);
                ComponentState state = new ComponentStateImpl((StandardNodeModule) module);
                state.deserialize(member);
                if (state.getProvider() == null) throw new MissingProviderException(state.getProviderID());
                Node node = state.getProvider().fromState(module, state);
                ((NodeImpl) node).impl_getPeer().dropPointProperty().set(new Point2D(node.getState().getX(), node.getState().getY()));
                ((AbstractNodeModule) module).nodes().add(node);
                for (NodeParameter parameter : node.getParameters()) {
                    parameterMap.put(parameter.getID(), parameter);
                }
            } catch (MissingProviderException e) {
                if (!ignoreMissingProvider) throw e;
            }
        }
        members = section.getList("links");
        for (int i = 0; i < members.size(); i++) {
            MapSection member = members.getMap(i);
            LinkImpl link = new LinkImpl(module, UUID.fromString(member.getString("sourceId")), UUID.fromString(member.getString("targetId")));
            NodeParameter from = parameterMap.get(link.getSourceId());
            NodeParameter to = parameterMap.get(link.getTargetId());
            if (from == null || to == null) continue;
            link.setSource(from);
            link.setTarget(to);
            ((AbstractNodeModule)module).links().add(link);
        }
    }

    @Override
    public void loadHeadlessModule(NodeModule module, InputStream inputStream) {
        Section section = Section.loadYaml(new InputStreamReader(inputStream));
        ListSection members = section.getList("nodes");
        Map<UUID, NodeParameter> parameterMap = new HashMap<>();
        for (int i = 0; i < members.size(); i++) {
            MapSection member = members.getMap(i);
            ComponentState state = new HeadlessComponentStateImpl((HeadlessNodeModule) module);
            state.deserialize(member);
            if (state.getProvider() == null) continue;
            Node node = state.getProvider().fromState(module, state);
            ((AbstractNodeModule) module).nodes().add(node);
            for (NodeParameter parameter : node.getParameters()) {
                parameterMap.put(parameter.getID(), parameter);
            }
        }
        members = section.getList("links");
        for (int i = 0; i < members.size(); i++) {
            MapSection member = members.getMap(i);
            LinkImpl link = new LinkImpl(module, UUID.fromString(member.getString("sourceId")), UUID.fromString(member.getString("targetId")));
            NodeParameter from = parameterMap.get(link.getSourceId());
            NodeParameter to = parameterMap.get(link.getTargetId());
            if (from == null || to == null) continue;
            link.setSource(from);
            link.setTarget(to);
            ((AbstractNodeModule)module).links().add(link);
        }
    }

    @Override
    public void saveModule(NodeModule module, OutputStream outputStream) {
        ModuleEditorState editor = module.getEditorState();
        Section section = Section.newMap();
        section.set(Section.newMap(), "editor");
        section.set(editor.getOffsetX(), "editor", "offsetX");
        section.set(editor.getOffsetY(), "editor", "offsetY");
        section.set(editor.getPivotX(), "editor", "pivotX");
        section.set(editor.getPivotY(), "editor", "pivotY");
        section.set(editor.getZoom(), "editor", "zoom");
        ListSection groups = Section.newList();
        ListSection nodes = Section.newList();
        for (NodeGroup member : new ArrayList<>(module.getGroups())) {
            groups.add(member.getState().serialize());
        }
        for (Node member : new ArrayList<>(module.getNodes())) {
            nodes.add(member.getState().serialize());
        }
        ListSection links = Section.newList();
        for (Link link : new ArrayList<>(module.getLinks())) {
            MapSection linkSerialized = Section.newMap();
            linkSerialized.set(link.getSourceId().toString(), "sourceId");
            linkSerialized.set(link.getTargetId().toString(), "targetId");
            links.add(linkSerialized);
        }
        section.set(groups, "groups");
        section.set(links, "links");
        section.set(nodes, "nodes");
        Section.saveYaml(section, new OutputStreamWriter(outputStream));
    }

    public static boolean isSerializable(Object o) {
        return o instanceof Number || o instanceof String || o instanceof Boolean || o instanceof Character || o instanceof Enum || o instanceof Field || o instanceof List || o instanceof Map;
    }

    private Set<NodeProviderCategory> categories = new HashSet<>();

    public Set<NodeProviderCategory> getCategories() {
        return categories;
    }

    @Override
    public NodeProvider getProvider(String id) {
        for (NodeProviderCategory category : categories) {
            NodeProvider provider = category.findProvider(id);
            if (provider != null) {
                return provider;
            }
            continue;
        }
        return new UnknownProvider(id);
    }

    @Override
    public void registerCategory(NodeProviderCategory category) {
        categories.add(category);
    }

    public void unregisterCategory(NodeProviderCategory category) {
        categories.remove(category);
    }

    public ParameterEditor getFallthroughEditor(Class<?> typeName) {
        return new FallbackEditor(null, typeName.getSimpleName(), toString(typeName), typeName);
    }

    public ParameterEditor getFallthroughEditor(GenericTypeStorage storage, Type typeName) {
        return new FallbackEditor(storage, parameterizedTypeSimpleName(storage, typeName), toString(typeName), typeName);
    }

    public static class FallbackEditor implements ParameterEditor {
        private GenericTypeStorage storage;
        private String subtext;
        private String tooltip;
        private Type generic;

        public FallbackEditor(GenericTypeStorage storage, String subtext, String tooltip, Type t) {
            this.storage = storage;
            this.subtext = subtext;
            this.tooltip = tooltip;
            generic = t;
        }

        public GenericTypeStorage getStorage() {
            return storage;
        }

        public Type getGeneric() {
            return generic;
        }

        @Override
        public Object createPeer(NodeParameter parameter) {
            return new SpecificParameter(parameter.getName(), subtext, tooltip);
        }
    }

    public static String parameterizedTypeSimpleName(GenericTypeStorage storage, Type type) {
        if (type == null) return "null";
        if (type instanceof ParameterizedType) {
            Type raw = ((ParameterizedType) type).getRawType();
            Type[] args = ((ParameterizedType) type).getActualTypeArguments();
            if (args.length == 0) return parameterizedTypeSimpleName(storage, raw);
            return parameterizedTypeSimpleName(storage, raw) + "<" + Arrays.stream(args).map(x -> parameterizedTypeSimpleName(storage, x)).collect(Collectors.joining(", ")) + ">";
        }
        if (type instanceof WildcardType) {
            Type[] upper = ((WildcardType) type).getUpperBounds();
            Type[] lower = ((WildcardType) type).getLowerBounds();
            if (lower != null && lower.length > 0) {
                return "? super "+Arrays.stream(upper).map(x -> parameterizedTypeSimpleName(storage, x)).collect(Collectors.joining(" & "));
            }
            if (upper != null && upper.length > 0) {
                return "? extends "+Arrays.stream(upper).map(x -> parameterizedTypeSimpleName(storage, x)).collect(Collectors.joining(" & "));
            }
            return "?";
        }
        if (type instanceof GenericArrayType) {
            return parameterizedTypeSimpleName(storage, ((GenericArrayType) type).getGenericComponentType()) + "[]";
        }
        if (type instanceof Class) return ((Class<?>) type).getSimpleName();
        if (type instanceof TypeVariable) {
            if (storage == null) return ((TypeVariable<?>) type).getName();
            Type result = storage.get((TypeVariable) type);
            if (result == null) return ((TypeVariable<?>) type).getName();
            return parameterizedTypeSimpleName(storage, result);
        }
        String name = type.getTypeName();
        int index = name.lastIndexOf('.');
        if (index >= 0) {
            name = name.substring(index);
        }
        return name;
    }

    public ParameterEditor getEditorForContentType(GenericTypeStorage storage, Class<?> typeName, Type generic) {
        if (Enum.class.isAssignableFrom(typeName)) {
            return parameter -> new PreEnumParameter(parameter.getName(), typeName);
        }
        if (Boolean.class.isAssignableFrom(typeName) || boolean.class.equals(typeName)) {
            return parameter -> new BooleanParameter(parameter.getName());
        }
        if (Character.class.isAssignableFrom(typeName) || char.class.equals(typeName)) {
            return parameter -> new CharacterParameter(parameter.getName());
        }
        if (Number.class.isAssignableFrom(typeName) || typeName.isPrimitive()) {
            return parameter -> new NumberParameter(parameter.getName(), typeName);
        }
        if (String.class.isAssignableFrom(typeName)) {
            return parameter -> new StringParameter(parameter.getName());
        }
        if (NodeFlow.getApplication().getBundleManager().findClass(BaseComponent[].class.getName()).isAssignableFrom(typeName)) {
            return parameter -> new BaseComponentParameter(parameter.getName());
        }
        return getFallthroughEditor(storage, generic);
    }

    @Override
    public ParameterEditor getEditorForContentType(Class<?> typeName) {
        if (Enum.class.isAssignableFrom(typeName)) {
            return parameter -> new PreEnumParameter(parameter.getName(), typeName);
        }
        if (Boolean.class.isAssignableFrom(typeName) || boolean.class.equals(typeName)) {
            return parameter -> new BooleanParameter(parameter.getName());
        }
        if (Character.class.isAssignableFrom(typeName) || char.class.equals(typeName)) {
            return parameter -> new CharacterParameter(parameter.getName());
        }
        if (Number.class.isAssignableFrom(typeName) || typeName.isPrimitive()) {
            return parameter -> new NumberParameter(parameter.getName(), typeName);
        }
//        if (BaseComponent[].class.isAssignableFrom(typeName)) {
        if (NodeFlow.getApplication().getBundleManager().findClass(BaseComponent[].class.getName()).isAssignableFrom(typeName)) {
            return parameter -> new BaseComponentParameter(parameter.getName());
        }
        if (String.class.isAssignableFrom(typeName)) {
            return parameter -> new StringParameter(parameter.getName());
        }
        if (ExecutionNodeParameter.class == typeName) {
            return parameter -> new LabelParameter(parameter.getName());
        }
        return getFallthroughEditor(typeName);
    }

    public static String toString(Type type) {
        if (type instanceof Class) {
            return toString((Class<?>) type);
        }
        if (type instanceof ParameterizedType) {
            StringBuilder builder = new StringBuilder();
            builder.append('<');
            int index = 0;
            for (Type gen : ((ParameterizedType) type).getActualTypeArguments()) {
                if (index > 0) builder.append(", ");
                builder.append(toString(gen));
                index++;
            }
            builder.append('>');
            return toString(((ParameterizedType) type).getRawType()) + builder;
        }
        return type == null ? "Unknown Type" : type.getTypeName();
    }

    public static String toString(Class<?> type) {
        if (type.isArray()) {
            return "Array of " + toString(type.getComponentType());
        }
        if (type.equals(int.class)) {
            return "Number";
        }
        if (type.equals(double.class)) {
            return "Number";
        }
        if (type.equals(float.class)) {
            return "Number";
        }
        if (type.equals(short.class)) {
            return "Number";
        }
        if (type.equals(char.class)) {
            return "Character";
        }
        if (type.equals(long.class)) {
            return "Number";
        }
        if (type.equals(boolean.class)) {
            return "Boolean";
        }
        if (type.equals(byte.class)) {
            return "Number";
        }
        if (type.equals(String.class)) {
            return "String";
        }
        if (type.equals(Object.class)) {
            return "Object";
        }
        return type.getName();
    }

    public static String toStringSimple(Class<?> type) {
        if (type.isArray()) {
            return "Array of " + toString(type.getComponentType());
        }
        if (type.equals(int.class)) {
            return "Number";
        }
        if (type.equals(double.class)) {
            return "Number";
        }
        if (type.equals(float.class)) {
            return "Number";
        }
        if (type.equals(short.class)) {
            return "Number";
        }
        if (type.equals(char.class)) {
            return "Character";
        }
        if (type.equals(long.class)) {
            return "Number";
        }
        if (type.equals(boolean.class)) {
            return "Boolean";
        }
        if (type.equals(byte.class)) {
            return "Number";
        }
        if (type.equals(String.class)) {
            return "String";
        }
        if (type.equals(Object.class)) {
            return "Object";
        }
        return type.getSimpleName();
    }

    @Override
    public EventProviderCategory createEventProviderCategory(ProjectFacet facet, String alias, String name, Icon icon) {
        EventProviderCategory category = new EventProviderCategoryImpl(facet, name, alias, icon);
        getCategories().add(category);
        return category;
    }

}
