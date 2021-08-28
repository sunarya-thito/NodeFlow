package thito.nodeflow.javadoc;

public interface ParserEngine {

    Handler createHandler(JavaDocSource source);
    interface Handler {
        JavaClass requestClass(String classPath);
        String[] getAllClassPaths();
    }

}
