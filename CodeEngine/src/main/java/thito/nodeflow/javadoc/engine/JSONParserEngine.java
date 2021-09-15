package thito.nodeflow.javadoc.engine;

import com.google.gson.*;
import thito.nodeflow.javadoc.*;

public class JSONParserEngine implements ParserEngine {
    private static final Gson gson = new Gson();
    @Override
    public Handler createHandler(JavaDocSource source) {
        return new JSONParserHandler(source);
    }

    public class JSONParserHandler implements Handler {
        private JavaDocSource source;

        public JSONParserHandler(JavaDocSource source) {
            this.source = source;
        }

        @Override
        public JavaClass requestClass(String classPath) {
            return gson.fromJson(source.getDocumentation(classPath), JavaClass.class);
        }

        @Override
        public String[] getAllClassPaths() {
            return gson.fromJson(source.getSourceData(), String[].class);
        }
    }
}
