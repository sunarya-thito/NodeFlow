package thito.nodeflow.javadoc;

import thito.nodeflow.javadoc.element.*;

public class JavaDocParser {
    private JavaDocSource source;
    private ParserEngine.Handler handler;

    public JavaDocParser(JavaDocSource source, ParserEngine engine) {
        this.source = source;
        this.handler = engine.createHandler(source);
    }

    public JavaDocSource getSource() {
        return source;
    }

    public ParserEngine.Handler getHandler() {
        return handler;
    }

    public String[] getAllClasses() {
        return handler.getAllClassPaths();
    }

    public JavaClass getDocumentation(String moduleName, String className) {
        return handler.requestClass(moduleName, className);
    }
}
