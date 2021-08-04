package thito.nodeflow.library.ui.code;

import javafx.beans.property.*;
import org.fxmisc.richtext.*;
import org.fxmisc.richtext.model.*;
import org.fxmisc.richtext.util.*;

import java.util.*;
import java.util.regex.*;

public class CodeTextArea extends CodeArea {
    private ObjectProperty<Highlighter> highlighter = new SimpleObjectProperty<Highlighter>() {
        @Override
        protected void invalidated() {
            applyHighlighter();
        }
    };


    public CodeTextArea(EditableStyledDocument<Collection<String>, String, Collection<String>> document) {
        super(document);
    }

    public CodeTextArea() {
        super();
        addListener();
        setUndoManager(UndoUtils.noOpUndoManager());
    }

    public CodeTextArea(String text) {
        super(text);
    }

    private void addListener() {
        multiPlainChanges().addObserver(e -> {
            applyHighlighter();
        });
    }

    public void applyHighlighter() {
        Highlighter highlighter = getHighlighter();
        if (highlighter == null) {
            highlighter = Highlighter.PLAIN;
        }
        if (getLength() > 0) {
            Pattern pattern = highlighter.getPattern();
            Matcher matcher = pattern.matcher(getText());
            int i = 0;
            StyleSpansBuilder<Collection<String>> builder = new StyleSpansBuilder<>();
            while (matcher.find()) {
                String styleClass = null;
                for (HighlightPattern highlightPattern : highlighter.getPatterns()) {
                    if (matcher.group(highlightPattern.getName()) != null) styleClass = highlighter.getName()+"-"+highlightPattern.getName();
                }
                builder.add(Collections.singletonList("highlighted-text"), matcher.start() - i);
                builder.add(Collections.singletonList(styleClass), matcher.end() - matcher.start());
                i = matcher.end();
            }
            builder.add(Collections.singletonList("highlighted-text"), getLength() - i);
            setStyleSpans(0, builder.create());
        }
    }

    public Highlighter getHighlighter() {
        return highlighter.get();
    }

    public ObjectProperty<Highlighter> highlighterProperty() {
        return highlighter;
    }

    public void setHighlighter(Highlighter highlighter) {
        this.highlighter.set(highlighter);
    }
}
