package thito.nodeflow.plugin.base.blueprint_legacy;

import javafx.beans.binding.*;
import javafx.collections.*;
import javafx.scene.paint.*;
import thito.nodeflow.internal.binding.CombinedListBinding;

import java.lang.reflect.*;
import java.util.*;

public class BlueprintRegistry {

    private final ObservableList<NodeProviderCategory> categories = FXCollections.observableArrayList();
    private static final Map<Class<?>, Color> typeColorMap = new HashMap<>();

    public static ObjectBinding<Color> getTypeColor(GenericStorage genericStorage, Type type) {
        GenericStorage.GenericBinding genericBinding = genericStorage.genericBinding(type);
        return Bindings.createObjectBinding(() -> getTypeColor(genericStorage.getRawClass(genericBinding.get())), genericBinding);
    }

    public static Color getTypeColor(Class<?> type) {
        return typeColorMap.computeIfAbsent(type, t -> {
            Random random = new Random();
            return Color.rgb(100 + random.nextInt(155), 100 + random.nextInt(155), 100 + random.nextInt(155));
        });
    }

    public static Map<Class<?>, Color> getTypeColorMap() {
        return typeColorMap;
    }

    public NodeProvider getProviderById(String id) {
        for (NodeProviderCategory category : getCategories()) {
            for (NodeProvider provider : category.getNodeProviders()) {
                if (provider.getId().equals(id)) {
                    return provider;
                }
            }
        }
        return BlueprintManager.getManager().getUnknownNodeProvider();
    }

    public ObservableList<NodeProviderCategory> getCategories() {
        ObservableList<NodeProviderCategory> categoryList = FXCollections.observableArrayList();
        CombinedListBinding.combine(categoryList, categories, BlueprintManager.getManager().getCategoryList());
        return categoryList;
    }
}
