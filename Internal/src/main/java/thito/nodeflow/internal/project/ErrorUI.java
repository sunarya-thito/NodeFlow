package thito.nodeflow.internal.project;

import com.jfoenix.controls.*;
import javafx.beans.property.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.internal.resource.*;
import thito.nodeflow.library.ui.layout.Component;
import thito.nodeflow.library.ui.layout.*;

import java.awt.*;
import java.io.*;
import java.util.*;

public class ErrorUI extends UIComponent {

    @Component("msg")
    private ObjectProperty<TextArea> msg = new SimpleObjectProperty<>();

    @Component("title")
    private ObjectProperty<Label> title = new SimpleObjectProperty<>();

    @Component("log")
    private ObjectProperty<JFXButton> log = new SimpleObjectProperty<>();

    @Component("copy")
    private ObjectProperty<JFXButton> copy = new SimpleObjectProperty<>();

    @Component("proceed")
    private ObjectProperty<JFXButton> proceed = new SimpleObjectProperty<>();

    private I18nItem message;
    private Throwable error;
    private Runnable proceedExec;
    public ErrorUI(I18nItem message, Throwable error, Runnable proceed) {
        this.message = message;
        this.error = error;
        this.proceedExec = proceed;
        setLayout(Layout.loadLayout("ErrorUI"));
    }

    @Override
    protected void onLayoutReady() {
        if (proceedExec == null) {
            ((Pane) proceed.get().getParent()).getChildren().remove(proceed.get());
        } else {
            proceed.get().setOnAction(e -> proceedExec.run());
        }
        msg.get().setEditable(false);
        title.get().textProperty().bind(message.stringBinding());
        StringWriter writer = new StringWriter();
        error.printStackTrace(new PrintWriter(writer));
        String text = writer.toString();
        msg.get().setText(text);
        copy.get().setOnAction(e -> {
            Map<DataFormat, Object> map = new HashMap<>();
            map.put(DataFormat.PLAIN_TEXT, text);
            Clipboard.getSystemClipboard().setContent(map);
        });
        log.get().setOnAction(e -> {
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().open(new File(ResourceManagerImpl.BASE_DIRECTORY, "logs"));
                } catch (IOException ioException) {
                }
            }
        });
    }
}
