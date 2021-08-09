package thito.nodeflow.bundled.editor;

import javafx.scene.layout.*;
import org.fxmisc.richtext.*;
import thito.nodeflow.api.*;
import thito.nodeflow.library.ui.code.*;

public class YamlViewport {
    private CodeTextArea code = new CodeTextArea();

    public YamlViewport(String text) {
        code.getStylesheets().add("rsrc:themes/"+ NodeFlow.getApplication().getUIManager().getTheme().getName()+"/Syntax.css");
        code.setBackground(Background.EMPTY);
        code.setParagraphGraphicFactory(LineNumberFactory.get(code));
        code.appendText(text);
        code.setHighlighter(Highlighter.YAML);
    }

    public CodeTextArea getCode() {
        return code;
    }
}
