package thito.nodeflow.internal.ui.editor;

import javafx.event.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import thito.nodeflow.internal.ui.*;
import thito.nodeflow.internal.ui.Skin;

import java.io.*;

public class ErrorTabSkin extends Skin {

    @Component("description")
    Label description;

    private Throwable error;

    public ErrorTabSkin(Throwable error) {
        this.error = error;
    }

    @Override
    protected void initializeSkin() {
        super.initializeSkin();
        registerActionHandler("copy-stacktrace", ActionEvent.ACTION, event -> {
            event.consume();
            ClipboardContent content = new ClipboardContent();
            StringWriter writer = new StringWriter();
            error.printStackTrace(new PrintWriter(writer));
            content.putString(writer.toString());
            Clipboard.getSystemClipboard().setContent(content);
        });
    }

    @Override
    protected void onLayoutLoaded() {
        StringWriter writer = new StringWriter();
        error.printStackTrace(new PrintWriter(writer));
        description.setText(writer.toString());
    }
}
