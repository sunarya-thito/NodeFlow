package thito.nodeflow.javadoc.engine;

import com.google.gson.*;
import thito.nodeflow.javadoc.*;
import thito.nodeflow.javadoc.element.*;
import thito.nodeflow.javadoc.element.reference.*;

import java.lang.reflect.*;

public class JSONParserEngine implements ParserEngine {
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(TypeReference.class, new TypeReferenceParser())
            .registerTypeAdapter(JavaMember.class, new JavaMemberParser())
            .create();
    @Override
    public Handler createHandler(JavaDocSource source) {
        return new JSONParserHandler(source);
    }

    public static class JavaMemberParser implements JsonDeserializer<JavaMember> {
        @Override
        public JavaMember deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String memberType = json.getAsJsonObject().get("memberType").getAsString();
            if (memberType.equals("JavaClass")) {
                return context.deserialize(json, JavaClass.class);
            }
            if (memberType.equals("JavaMethod")) {
                return context.deserialize(json, JavaMethod.class);
            }
            if (memberType.equals("JavaField")) {
                return context.deserialize(json, JavaField.class);
            }
            throw new IllegalArgumentException("invalid member type "+memberType);
        }
    }

    public static class TypeReferenceParser implements JsonDeserializer<TypeReference> {
        @Override
        public TypeReference deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String type = json.getAsJsonObject().get("type").getAsString();
            if (type.equals("Class")) {
                return context.deserialize(json, ClassTypeReference.class);
            }
            if (type.equals("Wildcard")) {
                return context.deserialize(json, WildcardTypeReference.class);
            }
            if (type.equals("Variable")) {
                return context.deserialize(json, VariableTypeReference.class);
            }
            throw new IllegalArgumentException("invalid type "+type);
        }
    }

    public class JSONParserHandler implements Handler {
        private JavaDocSource source;

        public JSONParserHandler(JavaDocSource source) {
            this.source = source;
        }

        @Override
        public JavaClass requestClass(String moduleName, String classPath) {
            return gson.fromJson(source.getDocumentation(moduleName, classPath), JavaClass.class);
        }

        @Override
        public String[] getAllClassPaths() {
            return gson.fromJson(source.getSourceData(), String[].class);
        }
    }
}
