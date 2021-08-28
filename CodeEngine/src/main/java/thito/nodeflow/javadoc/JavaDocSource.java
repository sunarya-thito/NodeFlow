package thito.nodeflow.javadoc;

public interface JavaDocSource {
    String getJavaDocVersion();
    String getDocumentation(String className);
    String getAllClassesPage();
}
