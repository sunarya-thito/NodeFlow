package thito.nodeflow.javadoc.source;

import thito.nodeflow.javadoc.*;

import java.io.*;
import java.net.*;
import java.nio.charset.*;

public class GeneralizedJavaDocsSource implements JavaDocSource {
    private String branch;

    public GeneralizedJavaDocsSource(String branch) {
        this.branch = branch;
    }

    @Override
    public String getSourceData() {
        return download("https://raw.githubusercontent.com/sunarya-thito/GeneralizedJavaDocs/" + branch + "/class-list.json");
    }

    @Override
    public String getDocumentation(String moduleName, String className) {
        return download("https://raw.githubusercontent.com/sunarya-thito/GeneralizedJavaDocs/" + branch + "/" + moduleName + "/" + className.replace('.', '/') + ".json");
    }

    private String download(String url) {
        StringBuilder builder = new StringBuilder();
        try (InputStreamReader inputStream = new InputStreamReader(new URL(url).openStream(), StandardCharsets.UTF_8)) {
            char[] buffer = new char[1024 * 8];
            int len;
            while ((len = inputStream.read(buffer, 0, buffer.length)) != -1) {
                builder.append(buffer, 0, len);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return builder.toString();
    }
}
