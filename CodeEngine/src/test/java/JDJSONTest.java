import com.google.gson.*;
import thito.nodeflow.javadoc.*;
import thito.nodeflow.javadoc.engine.*;
import thito.nodeflow.javadoc.source.*;

public class JDJSONTest {
    public static void main(String[] args) throws Throwable {
        JavaDocParser parser = new JavaDocParser(new GeneralizedJavaDocsSource("jdk-16"), new JSONParserEngine());
        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(parser.getDocumentation("java.base", "java.util.List")));
    }
}
