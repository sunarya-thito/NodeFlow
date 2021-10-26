package thito.nodeflow.plugin.base.blueprint_legacy;

import thito.nodeflow.plugin.base.blueprint_legacy.provider.ConstructorNodeProvider;
import thito.nodeflow.plugin.base.blueprint_legacy.provider.GetFieldNodeProvider;
import thito.nodeflow.plugin.base.blueprint_legacy.provider.MethodInvocationNodeProvider;
import thito.nodeflow.plugin.base.blueprint_legacy.provider.SetFieldNodeProvider;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class JavaNodeProviderCategory extends NodeProviderCategory {
    private Class<?> type;

    public JavaNodeProviderCategory(Class<?> type) {
        this.type = type;
        scan();
    }

    void scan() {
        for (Field field : type.getDeclaredFields()) {
            if (Modifier.isPublic(field.getModifiers())) {
                GetFieldNodeProvider getFieldNodeProvider = new GetFieldNodeProvider(field);
                getNodeProviders().add(getFieldNodeProvider);
                if (!Modifier.isFinal(field.getModifiers())) {
                    SetFieldNodeProvider setFieldNodeProvider = new SetFieldNodeProvider(field);
                    getNodeProviders().add(setFieldNodeProvider);
                }
            }
        }
        for (Method method : type.getDeclaredMethods()) {
            if (Modifier.isPublic(method.getModifiers())) {
                MethodInvocationNodeProvider methodInvocationNodeProvider = new MethodInvocationNodeProvider(method, false);
                getNodeProviders().add(methodInvocationNodeProvider);
                if (method.isVarArgs()) {
                    MethodInvocationNodeProvider varargsMethodInvocationNodeProvider = new MethodInvocationNodeProvider(method, true);
                    getNodeProviders().add(varargsMethodInvocationNodeProvider);
                }
            }
        }
        for (Constructor<?> constructor : type.getDeclaredConstructors()) {
            if (Modifier.isPublic(constructor.getModifiers())) {
                ConstructorNodeProvider constructorNodeProvider = new ConstructorNodeProvider(constructor, false);
                getNodeProviders().add(constructorNodeProvider);
                if (constructor.isVarArgs()) {
                    ConstructorNodeProvider varargsConstructorNodeProvider = new ConstructorNodeProvider(constructor, true);
                    getNodeProviders().add(constructorNodeProvider);
                }
            }
        }
    }
}
