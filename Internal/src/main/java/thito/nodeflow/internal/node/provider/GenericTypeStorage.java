package thito.nodeflow.internal.node.provider;

import javafx.collections.*;
import thito.nodeflow.api.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

public class GenericTypeStorage {

    private ObservableMap<TypeVariable, TypeReferenceCounter> clazz = FXCollections.observableHashMap();

    public class TypeReferenceCounter {
        TypeVariable key;
        Type type;
        int count;

        private TypeReferenceCounter(TypeVariable key, Type type) {
            this.key = key;
            this.type = type;
        }

        private void inc() {
            count++;
        }

        private void dec() {
            count--;
        }
    }

    public GenericTypeStorage() {
    }

    private boolean bounds;
    public ObservableMap<TypeVariable, TypeReferenceCounter> getMap() {
        return clazz;
    }

    public void setBounds(boolean bounds) {
        this.bounds = bounds;
    }

    public Type parameterizeType(Type type) {
        if (type instanceof TypeVariable) {
            Type result = get((TypeVariable) type);
            return result == null ? type : result;
        }
        if (type instanceof GenericArrayType) {
            return new GenericArrayTypeImpl(parameterizeType(((GenericArrayType) type).getGenericComponentType()));
        }
        if (type instanceof ParameterizedType) {
            Type[] actual = ((ParameterizedType) type).getActualTypeArguments();
            Type[] newActual = new Type[actual.length];
            for (int i = 0; i < actual.length; i++) {
                newActual[i] = parameterizeType(actual[i]);
            }
            return new ParameterizedTypeImpl(parameterizeType(((ParameterizedType) type).getRawType()), ((ParameterizedType) type).getOwnerType(), newActual);
        }
        if (type instanceof WildcardType) {
            Type[] upper = ((WildcardType) type).getUpperBounds();
            Type[] newUpper = new Type[upper.length];
            for (int i = 0; i < upper.length; i++) {
                newUpper[i] = parameterizeType(upper[i]);
            }
            Type[] lower = ((WildcardType) type).getLowerBounds();
            Type[] newLower = new Type[lower.length];
            for (int i = 0; i < lower.length; i++) {
                newLower[i] = parameterizeType(lower[i]);
            }
            return new WildcardTypeImpl(newUpper, newLower);
        }
        return type;
    }

    public Type get(TypeVariable variable) {
        TypeReferenceCounter type = clazz.get(variable);
        if (type == null) return null;
        return type.type instanceof Class ? parameterize((Class<?>) type.type) : type.type;
    }

    public void set(TypeVariable variable, Type type) {
        if (variable.equals(type) || bounds) return;
        TypeReferenceCounter counter = clazz.computeIfAbsent(variable, var -> new TypeReferenceCounter(var, type));
        if (counter.type.equals(type)) {
            counter.inc();
        }
    }

    public void remove(GenericTypeStorage parent, Type generic, Type type) {
        if (bounds) return;
        if (generic instanceof TypeVariable) {
            if (type instanceof TypeVariable) {
                Type result = parent.get((TypeVariable) type);
                if (result != null) {
                    type = result;
                }
            }
            TypeReferenceCounter counter = clazz.get(generic);
            if (counter != null && counter.type.equals(type)) {
                counter.dec();
                if (counter.count <= 0) {
                    clazz.remove(generic);
                }
            }
        } else if (generic instanceof ParameterizedType) {
            if (type instanceof ParameterizedType) {
                Type[] actual = ((ParameterizedType) generic).getActualTypeArguments();
                Type[] input = ((ParameterizedType) type).getActualTypeArguments();
                if (actual.length == input.length) {
                    for (int i = 0; i < actual.length; i++) {
                        remove(parent, actual[i], input[i]);
                    }
                }
            }
        } else if (generic instanceof GenericArrayType) {
            if (type instanceof GenericArrayType) {
                Type component = ((GenericArrayType) generic).getGenericComponentType();
                remove(parent, component, ((GenericArrayType) type).getGenericComponentType());
            } else if (type instanceof Class) {
                Type component = ((GenericArrayType) generic).getGenericComponentType();
                remove(parent, component, ((Class<?>) type).getComponentType());
            }
        } else if (generic instanceof WildcardType) {
            Type[] upper = ((WildcardType) generic).getUpperBounds();
            for (int i = 0; i < upper.length; i++) {
                if (upper[i] instanceof TypeVariable) {
                    remove(parent, upper[i], type);
                }
            }
        }
    }

    private static String toString(Type type) {
        if (type instanceof TypeVariable) {
            return ((TypeVariable<?>) type).getGenericDeclaration()+"("+((TypeVariable<?>) type).getName()+")";
        }
        return type.toString();
    }

    public void put(GenericTypeStorage parent, Type generic, Type type) {
        if (bounds) return;
        if (generic instanceof TypeVariable) {
            if (type instanceof TypeVariable) {
                Type result = parent.get((TypeVariable) type);
                if (result != null) {
                    type = result;
                }
            }
            set((TypeVariable) generic, type);
        } else if (generic instanceof ParameterizedType) {
            if (type instanceof ParameterizedType) {
                Type[] actual = ((ParameterizedType) generic).getActualTypeArguments();
                Type[] input = ((ParameterizedType) type).getActualTypeArguments();
                if (actual.length == input.length) {
                    for (int i = 0; i < actual.length; i++) {
                        put(parent, actual[i], input[i]);
                    }
                }
            }
        } else if (generic instanceof GenericArrayType) {
            if (type instanceof GenericArrayType) {
                Type component = ((GenericArrayType) generic).getGenericComponentType();
                put(parent, component, ((GenericArrayType) type).getGenericComponentType());
            } else if (type instanceof Class) {
                Type component = ((GenericArrayType) generic).getGenericComponentType();
                put(parent, component, ((Class<?>) type).getComponentType());
            }
        } else if (generic instanceof WildcardType) {
            Type[] upper = ((WildcardType) generic).getUpperBounds();
            for (int i = 0; i < upper.length; i++) {
                if (upper[i] instanceof TypeVariable) {
                    put(parent, upper[i], type);
                }
            }
        }
    }

//    public Section serialize() {
//        Section main = new MapSectionImpl();
//        for (Map.Entry<TypeVariable, Type> entry : clazz.entrySet()) {
//            String name = getClassName(entry.getValue());
//            if (name == null) continue;
//            main.set(name, toString(entry.getKey()));
//        }
//        return main;
//    }
//
//    public void deserialize(Section section) {
//        for (String key : ((MapSection) section).keySet()) {
//            try {
//                TypeVariable variable = fromString(key);
//                String value = section.getString(key);
//                Class<?> clazz = NodeFlow.getApplication().getBundleManager().findClass(value);
//                this.clazz.put(variable, parameterize(clazz));
//            } catch (Exception e) {
//            }
//        }
//    }

    private Type parameterize(Class<?> clazz) {
        TypeVariable[] variables = clazz.getTypeParameters();
        if (variables == null || variables.length == 0) {
            return clazz;
        }
        Type[] actualVariables = new Type[variables.length];
        boolean hasParam = false;
        for (int i = 0; i < variables.length; i++) {
            TypeVariable variable = variables[i];
            Type known = get(variable);
            actualVariables[i] = known == null ? variable : known;
            hasParam = hasParam || known != null;
        }
        return new ParameterizedTypeImpl(clazz, null, actualVariables);
    }

    private static String getClassName(Type type) {
        if (type instanceof ParameterizedType) {
            return getClassName(((ParameterizedType) type).getRawType());
        }
        if (type instanceof Class) {
            return ((Class<?>) type).getName();
        }
        if (type instanceof TypeVariable) {
            return ((Type) ((TypeVariable<?>) type).getGenericDeclaration()).getTypeName()+":"+((TypeVariable<?>) type).getName();
        }
        return null;
    }

    private static String toString(TypeVariable variable) {
        if (variable.getGenericDeclaration() instanceof Method) {
            return "METHOD@"+((Method) variable.getGenericDeclaration()).getDeclaringClass().getName()+"&"+variable.getName()+"&"
                    + Arrays.stream(((Method) variable.getGenericDeclaration()).getParameterTypes()).map(x -> x.getName()).collect(Collectors.joining(";"))
                    + "&" + variable.getName();
        } else if (variable.getGenericDeclaration() instanceof Constructor) {
            return "CONSTRUCTOR&"+((Method) variable.getGenericDeclaration()).getDeclaringClass().getName()+"&"
                    + Arrays.stream(((Method) variable.getGenericDeclaration()).getParameterTypes()).map(x -> x.getName()).collect(Collectors.joining(";"))
                    + "&" + variable.getName();
        } else if (variable.getGenericDeclaration() instanceof Class) {
            return "CLASS&"+((Class<?>) variable.getGenericDeclaration()).getName()+"&"+variable.getName();
        } else throw new IllegalStateException("illegal implementation "+variable.getClass());
    }

    private static TypeVariable fromString(String string) throws Exception {
        String[] split = string.split("&");
        if (split[0].equals("METHOD")) {
            Class<?> clazz = NodeFlow.getApplication().getBundleManager().findClass(split[1]);
            Class<?>[] params = Arrays.stream(split[3].split(";")).map(NodeFlow.getApplication().getBundleManager()::findClass).toArray(Class[]::new);
            Method method = clazz.getMethod(split[2], params);
            for (TypeVariable variable : method.getTypeParameters()) {
                if (variable.getName().equals(split[4])) {
                    return variable;
                }
            }
            return null;
        } else if (split[0].equals("CONSTRUCTOR")) {
            Class<?> clazz = NodeFlow.getApplication().getBundleManager().findClass(split[1]);
            Class<?>[] params = Arrays.stream(split[2].split(";")).map(NodeFlow.getApplication().getBundleManager()::findClass).toArray(Class[]::new);
            Constructor constructor = clazz.getConstructor(params);
            for (TypeVariable variable : constructor.getTypeParameters()) {
                if (variable.getName().equals(split[4])) {
                    return variable;
                }
            }
            return null;
        } else if (split[0].equals("CLASS")) {
            Class<?> clazz = NodeFlow.getApplication().getBundleManager().findClass(split[1]);
            for (TypeVariable variable : clazz.getTypeParameters()) {
                if (variable.getName().equals(split[2])) {
                    return variable;
                }
            }
            return null;
        } else throw new IllegalStateException("illegal implementation "+split[0]);
    }
}
