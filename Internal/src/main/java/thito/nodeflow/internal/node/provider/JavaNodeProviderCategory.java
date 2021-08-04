package thito.nodeflow.internal.node.provider;

import org.apache.commons.lang.*;
import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.editor.node.java.*;
import thito.nodeflow.api.ui.*;
import thito.nodeflow.internal.node.provider.java.*;

import java.lang.reflect.*;
import java.util.*;
public class JavaNodeProviderCategory implements JavaProviderCategory {

    public static String capitalizeCamelCase(String string) {
        StringBuilder builder = new StringBuilder(string.length());
        boolean wasUppercase = false;
        boolean wasDigit = false;
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (c == '_') {
                builder.append(' ');
                continue;
            }
            if (Character.isUpperCase(c)) {
                if (!wasUppercase) {
                    builder.append(' ');
                }
                builder.append(wasDigit ? c : c);
            } else if (Character.isDigit(c)) {
                if (!wasDigit || wasUppercase) {
                    builder.append(' ');
                }
                builder.append(c);
            } else {
                if (i == 0 || wasDigit) {
                    if (wasDigit) {
                        builder.append(' ');
                    }
                }
                builder.append(c);
            }
            wasDigit = Character.isDigit(c);
            wasUppercase = Character.isUpperCase(c);
        }
        String[] split = builder.toString().trim().toLowerCase().split(" ");
        for (int i = 0; i < split.length; i++) {
            split[i] = StringUtils.capitalize(split[i]);
        }
        return String.join(" ", split);
    }

    private List<NodeProvider> providers = new ArrayList<>();
    private Class<?> clazz;

    public JavaNodeProviderCategory(Class<?> clazz) {
        this.clazz = clazz;
        providers.add(new StaticGetClass(clazz, this));
        providers.add(new InstanceOfBlock(clazz, this));
        if (Throwable.class.isAssignableFrom(clazz)) {
            providers.add(new TryCatchBlock(clazz, this));
        }
        providers.add(new TernaryBlock(clazz, this));
        providers.add(new EqualsCompare(this, clazz));
        providers.add(new CastProvider(clazz, this));
        if (clazz.isInterface()) {
            providers.add(new ImplementationNodeProvider(clazz, null, this));
        } else if (Modifier.isAbstract(clazz.getModifiers()) || !Modifier.isFinal(clazz.getModifiers())) {
            for (Constructor constructor : clazz.getDeclaredConstructors()) {
                if (Modifier.isPublic(constructor.getModifiers())) {
                    providers.add(new ImplementationNodeProvider(clazz, constructor, this));
                }
            }
        }
        if (!Modifier.isAbstract(clazz.getModifiers())) {
            for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
                if (Modifier.isPublic(constructor.getModifiers())) {
                    ConstructorNodeProvider varArgs = new ConstructorNodeProvider(constructor, 0, this);
                    if (varArgs.hasVarArgs()) {
                        providers.add(new ConstructorNodeProvider(constructor, 1, this));
                    }
                    providers.add(new ConstructorNodeProvider(constructor, -1, this));
                }
            }
        }
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isSynthetic()) continue;
            if (Modifier.isPublic(method.getModifiers()) || clazz.isInterface()) {
                MethodNodeProvider varArgs = new MethodNodeProvider(method,1, this, false);
                if (varArgs.hasVarArgs()) {
                    providers.add(varArgs);
                }
                providers.add(new MethodNodeProvider(method, -1, this, false));
            }
        }
        for (Method method : clazz.getMethods()) {
            if (method.isSynthetic()) continue;
            if (MethodOverrideNodeParameter.isOverridable(method.getModifiers())) {
                providers.add(new MethodNodeProvider(method, -1, this, true));
            }
        }
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isEnumConstant() || field.isSynthetic()) continue;
            if (Modifier.isPublic(field.getModifiers())) {
                if (!Modifier.isFinal(field.getModifiers())) {
                    providers.add(new SetFieldNodeProvider(field, this));
                }
                providers.add(new GetFieldNodeProvider(field, this));
            }
        }
    }

    @Override
    public NodeProvider findProvider(String id) {
        boolean constructor = id.startsWith("constructor(");
        boolean method = id.startsWith("method(");
        if (constructor || method) {
            String varargsLengthRaw = id.substring(id.indexOf('(') + 1);
            varargsLengthRaw = varargsLengthRaw.substring(0, varargsLengthRaw.indexOf(')'));
            String[] splitArgs = varargsLengthRaw.split(" ");
            int totalVarargs = Integer.parseInt(splitArgs[0]);
            boolean implementation = splitArgs.length > 1 && Boolean.parseBoolean(splitArgs[1]);
            String newId = id.substring(id.indexOf(')') + 1);
            String className = newId.substring(1, newId.indexOf('#'));
            if (className.equals(clazz.getName())) {
                String name = newId.substring(newId.indexOf('#') + 1, newId.indexOf('('));
                String[] parameters = newId.substring(newId.indexOf('(') + 1, newId.indexOf(')')).split(",");
                if (constructor) {
                    for (Constructor<?> cons : clazz.getDeclaredConstructors()) {
                        if (cons.getParameterCount() == parameters.length && Modifier.isPublic(cons.getModifiers())) {
                            boolean match = true;
                            Class<?>[] param = cons.getParameterTypes();
                            for (int i = 0; i < param.length; i++) {
                                if (!param[i].getName().equals(parameters[i])) {
                                    match = false;
                                }
                            }
                            if (match) {
                                return new ConstructorNodeProvider(cons, totalVarargs, this);
                            }
                        }
                    }
                } else if (method) {
                    for (Method meth : clazz.getDeclaredMethods()) {
                        if (meth.getName().equals(name) && meth.getParameterCount() == parameters.length && Modifier.isPublic(meth.getModifiers())) {
                            boolean match = true;
                            Class<?>[] param = meth.getParameterTypes();
                            for (int i = 0; i < param.length; i++) {
                                if (!param[i].getName().equals(parameters[i])) {
                                    match = false;
                                }
                            }
                            if (match) {
                                return new MethodNodeProvider(meth, totalVarargs, this, implementation);
                            }
                        }
                    }
                }
            }
        }
        return JavaProviderCategory.super.findProvider(id);
    }

    public Class<?> getType() {
        return clazz;
    }

    @Override
    public Icon getIcon() {
        return null;
    }

    @Override
    public String getName() {
        return clazz.getName();
    }

    @Override
    public String getAlias() {
        return clazz.getSimpleName();
    }

    @Override
    public List<NodeProvider> getProviders() {
        return providers;
    }
}
