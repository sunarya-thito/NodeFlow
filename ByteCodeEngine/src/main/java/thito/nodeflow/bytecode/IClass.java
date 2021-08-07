package thito.nodeflow.bytecode;

public interface IClass extends IMember {
    IClass[] getInterfaces();
    IClass getSuperClass();
    String getSimpleName();
    IField[] getFields();
    IField getField(String name);
    IField[] getDeclaredFields();
    IField getDeclaredField(String name);
    IMethod[] getMethods();
    IMethod getMethod(String name, IClass...parameterTypes);
    IMethod[] getDeclaredMethods();
    IMethod getDeclaredMethod(String name, IClass...parameterTypes);
    IConstructor[] getConstructors();
    IConstructor getConstructor(IClass...parameterTypes);
    IConstructor[] getDeclaredConstructors();
    IConstructor getDeclaredConstructor(IClass...parameterTypes);
    IClass[] getDeclaredClasses();
    IClass getDeclaredClass(String name);
    IClass[] getClasses();
    IClass getClass(String name);
    boolean isAssignableFrom(IClass type);
    boolean isArray();
    IClass getComponentType();

    default Reference field(String name) {
        return getField(name).get(null);
    }

    default MethodInvocation method(String name, IClass... parameterTypes) {
        return new MethodInvocation() {
            @Override
            public Reference invoke(Object... args) {
                return getMethod(name, parameterTypes).invoke(null, args);
            }

            @Override
            public void invokeVoid(Object... args) {
                getMethod(name, parameterTypes).invokeVoid(null, args);
            }
        };
    }

    interface MethodInvocation {
        Reference invoke(Object...args);
        void invokeVoid(Object...args);
    }
}
