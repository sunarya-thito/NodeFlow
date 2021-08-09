package thito.nodeflow.internal.node.provider;

import javafx.beans.*;
import javafx.collections.*;
import thito.nodeflow.api.*;
import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.node.*;
import thito.nodeflow.api.node.state.*;
import thito.nodeflow.internal.editor.*;
import thito.nodeflow.internal.node.*;
import thito.nodeflow.internal.node.parameter.*;
import thito.nodejfx.parameter.type.*;

import java.lang.reflect.*;
import java.util.*;

public class JavaNodeParameter implements NodeParameterFactory {

    public static Class<?> toWrapper(Class<?> clazz) {
        if (!clazz.isPrimitive())
            return clazz;
        if (clazz == Integer.TYPE)
            return Integer.class;
        if (clazz == Long.TYPE)
            return Long.class;
        if (clazz == Boolean.TYPE)
            return Boolean.class;
        if (clazz == Byte.TYPE)
            return Byte.class;
        if (clazz == Character.TYPE)
            return Character.class;
        if (clazz == Float.TYPE)
            return Float.class;
        if (clazz == Double.TYPE)
            return Double.class;
        if (clazz == Short.TYPE)
            return Short.class;
        if (clazz == Void.TYPE)
            return Void.class;

        return clazz;
    }

    private static Type toGeneric(Class<?> generic) {
        if (generic.isArray()) {
            return new GenericArrayTypeImpl(toGeneric(generic.getComponentType()));
        }
        if (generic.getTypeParameters().length > 0) {
            return new ParameterizedTypeImpl(generic);
        }
        return generic;
    }

    private static int countDimensions(GenericArrayType type) {
        if (type.getGenericComponentType() instanceof GenericArrayType) {
            return countDimensions((GenericArrayType) type.getGenericComponentType()) + 1;
        }
        return 1;
    }

    private static Class<?> array(int dimensions) {
        char[] x = new char[dimensions];
        Arrays.fill(x, '[');
        try {
            return Class.forName(new String(x)+"Ljava.lang.Object;", false, null);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static Class<?> fromGeneric(GenericTypeStorage storage, Type type) {
        if (type instanceof TypeVariable) {
            if (storage != null) {
                return fromGeneric(storage, storage.get((TypeVariable) type));
            }
        }
        if (type instanceof Class) {
            return (Class<?>) type;
        }
        if (type instanceof GenericArrayType) {
            return array(countDimensions((GenericArrayType) type));
        }
        if (type instanceof ParameterizedType) {
            return fromGeneric(storage, ((ParameterizedType) type).getRawType());
        }
        return Object.class;
    }

    private String name;
    private Class<?> clazz;
    private Type type;
    private boolean fallthrough, castable;
    private LinkMode input, output;
    private boolean removable, insertable;

    public JavaNodeParameter(String name, Type type, boolean fallthrough, LinkMode input, LinkMode output) {
        this(name, type, fallthrough, input, output, false);
    }

    public JavaNodeParameter(String name, Type type, boolean fallthrough, LinkMode input, LinkMode output, boolean castable) {
        this.name = name;
        this.type = type instanceof Class ? toGeneric((Class<?>) type) : type;
        this.fallthrough = fallthrough;
        this.input = input;
        this.output = output;
        this.castable = castable;
        clazz = toWrapper(fromGeneric(null, type));
    }

    public JavaNodeParameter removable() {
        removable = true;
        return this;
    }

    public JavaNodeParameter insertable() {
        insertable = true;
        return this;
    }

    @Override
    public LinkMode getOutputMode() {
        return output;
    }

    @Override
    public LinkMode getInputMode() {
        return input;
    }

    @Override
    public Class<?> getType() {
        return clazz;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public NodeParameter createParameter(Node node, ComponentParameterState state) {
        ParameterEditor override = null;
        if (node.getState().getProvider() instanceof MethodNodeProvider && node instanceof NodeImpl) {
            if (!((MethodNodeProvider) node.getState().getProvider()).isImplementation()) {
                int index = Arrays.asList(node.getState().getParameters()).indexOf(state);
                index = ((MethodNodeProvider) node.getState().getProvider()).getParameterMap().getOrDefault(index, -1);
                Method method = ((MethodNodeProvider) node.getState().getProvider()).getMethod();
                if (index >= 0 && index < method.getParameterCount()) {
                    MethodParameterCompleter completer =
                            ((EditorManagerImpl) NodeFlow.getApplication().getEditorManager()).getHandler(method, method.getParameters()[index]);
                    if (completer != null) {
                        int finalIndex = index;
                        override = parameter -> new StringCompleterParameter(parameter.getName(),
                                ((StandardNodeModule) node.getModule())
                                        .getProject(),
                                completer,
                                method,
                                method.getParameters()[finalIndex]);
                    }
                }
            }
        }
        GenericTypeStorage storage = node instanceof NodeImpl ? ((NodeImpl) node).getStorage() : null;
        NodeParameterImpl parameter;
        if ("nodeflow.spigotmc.Menu".equals(type.getTypeName()) && !fallthrough && override != null) {
            parameter = new ChestParameterImpl(state, name, node);
        } else {
            parameter = new NodeParameterImpl(state, name, node,
                    override != null ?
                            override :
                            fallthrough ?
                                    ModuleManagerImpl.getInstance().getFallthroughEditor(storage, type) :
                                    ModuleManagerImpl.getInstance().getEditorForContentType(storage, getType(), type),
                    new CompoundType().scan(type), type);
        }
        parameter.impl_getPeer().insertableProperty().set(insertable);
        parameter.setRemovable(removable);
        GenericTypeStorage finalStorage = storage;
        parameter.impl_getPeer().getUnmodifiableInputLinks().addListener((SetChangeListener<thito.nodejfx.NodeParameter>) change -> {
            if (change.wasAdded()) {
                NodeParameterImpl param = (NodeParameterImpl) change.getElementAdded().getUserData();
                Type type = param.getGenericType();
                if (type != null && finalStorage != null) {
                    finalStorage.put(((NodeImpl) param.getNode()).getStorage(), this.type, type);
                }
            }
            if (change.wasRemoved()) {
                NodeParameterImpl param = (NodeParameterImpl) change.getElementRemoved().getUserData();
                Type type = param.getGenericType();
                if (type != null && finalStorage != null) {
                    finalStorage.remove(((NodeImpl) param.getNode()).getStorage(), this.type, type);
                }
            }
        });
        parameter.impl_getPeer().getUnmodifiableOutputLinks().addListener((SetChangeListener<thito.nodejfx.NodeParameter>) change -> {
            if (change.wasAdded()) {
                NodeParameterImpl param = (NodeParameterImpl) change.getElementAdded().getUserData();
                Type type = param.getGenericType();
                if (type != null && finalStorage != null) {
                    finalStorage.put(((NodeImpl) param.getNode()).getStorage(), this.type, type);
                }
            }
            if (change.wasRemoved()) {
                NodeParameterImpl param = (NodeParameterImpl) change.getElementRemoved().getUserData();
                Type type = param.getGenericType();
                if (type != null && finalStorage != null) {
                    finalStorage.remove(((NodeImpl) param.getNode()).getStorage(), this.type, type);
                }
            }
        });
        if (storage != null) {
            storage.getMap().addListener((InvalidationListener) obs -> {
                parameter.setEditor(
                        fallthrough ?
                                ModuleManagerImpl.getInstance().getFallthroughEditor(finalStorage, type) :
                                ModuleManagerImpl.getInstance().getEditorForContentType(finalStorage, getType(), type)
                );
                parameter.setType(new CompoundType().scan(finalStorage.parameterizeType(type)));
            });
        }
        parameter.setInputMode(input);
        parameter.setOutputMode(output);
        return parameter;
    }

}
