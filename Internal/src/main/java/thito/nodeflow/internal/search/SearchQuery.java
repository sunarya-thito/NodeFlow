package thito.nodeflow.internal.search;

import java.util.*;

public class SearchQuery {
    private String text;
    private boolean regex, ignoreCase, multiLine;
    private List<SearchFilter> filters = new ArrayList<>();

    public SearchQuery(String text) {
        this.text = text;
    }

    public void setIgnoreCase(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }

    public void setMultiLine(boolean multiLine) {
        this.multiLine = multiLine;
    }

    public boolean isIgnoreCase() {
        return ignoreCase;
    }

    public boolean isMultiLine() {
        return multiLine;
    }

    public void setRegex(boolean regex) {
        this.regex = regex;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isRegex() {
        return regex;
    }

    public String getText() {
        return text;
    }

    public List<SearchFilter> getFilters() {
        return filters;
    }
}
