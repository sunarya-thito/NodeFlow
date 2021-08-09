package thito.nodeflow.library.ui.layout;

import org.jsoup.nodes.*;
import thito.nodeflow.api.*;
import thito.nodeflow.api.resource.*;

import java.io.*;

public class Layout {
    public static final LayoutParser GLOBAL_PARSER = new LayoutParser();
    private LayoutParser parser;
    private Document document;

    public Layout(LayoutParser parser, Document document) {
        this.parser = parser;
        this.document = document;
    }

    public static Layout loadLayout(String name) {
        return loadLayout(GLOBAL_PARSER, (ResourceFile) NodeFlow.getApplication().getResourceManager().getResource("layouts/"+name+".xml"));
    }

    public static Layout loadLayout(LayoutParser parser, ResourceFile resource) {
        StringWriter writer = new StringWriter();
        try (Reader reader = resource.openReader()) {
            char[] buff = new char[1024];
            int len;
            while ((len = reader.read(buff, 0, buff.length)) != -1) {
                writer.write(buff, 0, len);
            }
        } catch (Throwable t) {
            throw new RuntimeException("Failed to load layout "+resource.getPath(), t);
        }
        return new Layout(parser, parser.parseXML(writer.toString()));
    }

    public LayoutParser getParser() {
        return parser;
    }

    public Document getDocument() {
        return document;
    }
}
