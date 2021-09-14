package thito.nodeflow.plugin.base.blueprint;

import javafx.beans.*;
import javafx.beans.binding.*;
import javafx.collections.*;
import org.apache.commons.lang3.reflect.*;

import java.lang.reflect.*;
import java.util.*;

public class GenericStorage {
    private ObservableList<GenericStorage> lookUp = FXCollections.observableArrayList();
    private ObservableMap<TypeVariable<?>, Type> mapping = FXCollections.observableHashMap();
    private Set<GenericBinding> bindingSet = Collections.newSetFromMap(new WeakHashMap<>());

    public ObservableMap<TypeVariable<?>, Type> getMapping() {
        return mapping;
    }

    public GenericStorage() {
        mapping.addListener((InvalidationListener) obs -> updateRecursively());
    }

    public void updateRecursively() {
        updateRecursively(new LinkedHashSet<>());
    }

    private void updateRecursively(Set<GenericStorage> scanned) {
        if (scanned.add(this)) {
            update();
            for (GenericStorage child : lookUp) {
                child.updateRecursively(scanned);
            }
        }
    }

    public void update() {
        for (GenericBinding genericBinding : bindingSet) {
            genericBinding.invalidate();
        }
    }

    public Map<TypeVariable<?>, Type> getFullMapping() {
        Map<TypeVariable<?>, Type> map = new HashMap<>();
        scan(map, new LinkedHashSet<>());
        return map;
    }

    private void scan(Map<TypeVariable<?>, Type> map, Set<GenericStorage> scanned) {
        if (!scanned.add(this)) return;
        map.putAll(getMapping());
        for (GenericStorage storage : lookUp) {
            storage.scan(map, scanned);
        }
    }

    public StringBinding canonicalNameBinding(Type type) {
        return Bindings.createStringBinding(() -> computeCanonicalName(type), getMapping(), getLookUp());
    }

    public ObservableList<GenericStorage> getLookUp() {
        return lookUp;
    }

    public GenericBinding genericBinding(Type type) {
        GenericBinding genericBinding = new GenericBinding(type);
        bindingSet.add(genericBinding);
        return genericBinding;
    }

    public Class<?> getRawClass(Type type) {
        Class<?> rawType = TypeUtils.getRawType(type, null);
        return rawType == null ? Object.class : rawType;
    }

    public Type fillOut(Type type) {
        return TypeUtils.unrollVariables(getMapping(), type);
    }

    public String computeCanonicalName(Type type) {
        return TypeUtils.toString(type);
    }

    public class GenericBinding extends ObjectBinding<Type> {
        private Type type;

        public GenericBinding(Type type) {
            this.type = type;
        }

        @Override
        protected Type computeValue() {
            return fillOut(type);
        }
    }

}
