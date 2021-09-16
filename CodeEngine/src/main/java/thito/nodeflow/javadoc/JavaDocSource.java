package thito.nodeflow.javadoc;

public interface JavaDocSource {
    String getSourceData();
    String getDocumentation(String moduleName, String className);
}
