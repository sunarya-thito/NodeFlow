package thito.nodeflow.javadoc;

public abstract class TypeReference {
    protected String type;
    protected Integer arrayDimensions;

    public void setArrayDimensions(int arrayDimensions) {
        this.arrayDimensions = arrayDimensions == 0 ? null : arrayDimensions;
    }

    public int getArrayDimensions() {
        return arrayDimensions == null ? 0 : arrayDimensions;
    }

    protected String toStringArray() {
        int arrayDimensions = getArrayDimensions();
        StringBuilder builder = new StringBuilder(arrayDimensions * 2);
        for (int i = 0; i < arrayDimensions; i++) builder.append("[]");
        return builder.toString();
    }
}
