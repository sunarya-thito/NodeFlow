package thito.nodeflow.javadoc;

import thito.nodeflow.javadoc.element.*;

public interface ParserEngine {

    Handler createHandler(JavaDocSource source);
    interface Handler {
        JavaClass requestClass(String moduleName, String classPath);
        String[] getAllClassPaths();
    }

}
