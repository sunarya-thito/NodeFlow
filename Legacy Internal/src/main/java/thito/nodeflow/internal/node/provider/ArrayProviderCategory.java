package thito.nodeflow.internal.node.provider;

import org.objectweb.asm.*;
import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.internal.node.*;
import thito.nodeflow.internal.node.provider.java.*;

import java.util.*;

public class ArrayProviderCategory extends SimpleNodeProviderCategory {

    public static Class<?> asArray(Class<?> clazz, int dimension) throws ClassNotFoundException {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < dimension; i++) builder.append('[');
        return Class.forName(builder + Type.getDescriptor(clazz).replace('/', '.'), false, clazz.getClassLoader());
    }

    private int dimension = 1;

    public ArrayProviderCategory() {
        super("Java Array", "Create Array", null);
    }

    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        initialized = false;
        this.dimension = dimension;
    }

    private Map<String, NodeProvider> cache = new HashMap<>();

    @Override
    public NodeProvider findProvider(String id) {
        if (id.startsWith("java#array://")) {
            id = id.substring(13);
            int totalDimension = Integer.parseInt(id.substring(id.indexOf('(') + 1, id.lastIndexOf(')')));
            id = id.substring(0, id.indexOf('('));
            for (NodeProviderCategory category : ModuleManagerImpl.getInstance().getCategories()) {
                if (category instanceof JavaNodeProviderCategory) {
                    if (((JavaNodeProviderCategory) category).getType().getName().equals(id)) {
                        NodeProvider provider = new ArrayProvider(((JavaNodeProviderCategory) category).getType(), totalDimension, this);
                        cache.put(id, provider);
                        return provider;
                    }
                }
            }
        }
        return super.findProvider(id);
    }

    private boolean initialized;

    @Override
    public List<NodeProvider> getProviders() {
        if (!initialized) {
            initialized = true;
            getProviders().clear();
            for (NodeProviderCategory category : ModuleManagerImpl.getInstance().getCategories()) {
                if (category instanceof JavaNodeProviderCategory) {
                    getProviders().add(new ArrayProvider(((JavaNodeProviderCategory) category).getType(), dimension, this));
                }
            }
        }
        return super.getProviders();
    }

}
