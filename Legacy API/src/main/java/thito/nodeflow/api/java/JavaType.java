package thito.nodeflow.api.java;

public interface JavaType {

    String getName();

    default String getSimpleName() {
        String name = getName();
        int index = name.indexOf('.');
        if (index >= 0) {
            name = name.substring(index + 1);
        }
        return name;
    }

    default String getInternalName() {
        return getName().replace('.', '/');
    }

    default String getDescriptor() {
        String name = getName();
        String type;
        switch (name) {
            case "int":
                type = "I";
                break;
            case "long":
                type = "J";
                break;
            case "byte":
                type = "B";
                break;
            case "char":
                type = "C";
                break;
            case "double":
                type = "D";
                break;
            case "float":
                type = "F";
                break;
            case "short":
                type = "S";
                break;
            case "boolean":
                type = "Z";
                break;
            default:
                type = "L"+getInternalName()+";";
        }
        return type;
    }

    JavaType getSuperClass();

    JavaType[] getInterfaces();

    boolean isAssignableFrom(JavaType type);
}
