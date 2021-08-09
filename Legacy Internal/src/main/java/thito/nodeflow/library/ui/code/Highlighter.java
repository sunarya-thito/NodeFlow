package thito.nodeflow.library.ui.code;

import javafx.beans.*;
import javafx.collections.*;

import java.util.regex.*;

public class Highlighter {
    public static final Highlighter PLAIN = new Highlighter("plain", $("text", ".*"));
    public static final Highlighter YAML = new Highlighter("yaml",
            $("comment", "#[^\\n]*"),
            $("key", "(?!\\s?-)^.[^#\\n]*?(?<!#)(?=:)"),
            $("colon", ":"),
            $("comma", ","),
            $("dash", "[ \\t\\n]+-"),
            $("boolean", "\\b(true|false)\\b"),
            $("string", "(?<!\\w)('(.*?)')|(\"(.*?)\")"),
            $("number", "-?\\d+(\\.\\d+)?"),
            $("bracket", "\\[|\\]")
            );
    public static final Highlighter PROPERTIES = new Highlighter("properties",
            $("comment", "#[^\\n]*"),
            $("equalSign", "="),
            $("key", "[^\\n]*(?==)"),
            $("value", "[^\\n]*")
            );
    public static final Highlighter XML = new Highlighter("xml",
            $("comment", "<!--[^<>]+-->"),
            $("tags", "(</?\\h*)(\\w+)([^<>]*)(\\h*/?>)")
            );

    private static HighlightPattern $(String name, String pattern) {
        return new HighlightPattern(name, pattern);
    }

    private ObservableList<HighlightPattern> patterns = FXCollections.observableArrayList();
    private Pattern pattern;
    private String name;

    public Highlighter(String name, HighlightPattern...patterns) {
        this.name = name;
        this.patterns.addAll(patterns);
        this.patterns.addListener((InvalidationListener) observable -> pattern = null);
    }

    public Pattern getPattern() {
        if (pattern == null) {
            pattern = compilePatterns();
        }
        return pattern;
    }

    public String getName() {
        return name;
    }

    public ObservableList<HighlightPattern> getPatterns() {
        return patterns;
    }

    private Pattern compilePatterns() {
        if(patterns.isEmpty()) return null;
        StringBuilder patternBuilder = new StringBuilder();
        for(HighlightPattern pattern : patterns) {
            patternBuilder.append("(?<").append(pattern.getName()).append(">").append(pattern.getPattern()).append(")|");
        }
        return Pattern.compile(patternBuilder.substring(0, patternBuilder.toString().length() - 1), Pattern.MULTILINE | Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
    }
}
