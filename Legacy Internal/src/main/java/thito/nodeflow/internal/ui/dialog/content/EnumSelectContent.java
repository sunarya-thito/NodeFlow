package thito.nodeflow.internal.ui.dialog.content;

import javafx.scene.layout.*;
import thito.nodeflow.api.ui.dialog.*;
import thito.nodeflow.api.ui.dialog.content.*;
import thito.nodeflow.internal.ui.editor.*;

import java.lang.reflect.*;
import java.util.function.*;

public class EnumSelectContent implements DialogContent {
    private Class<?> javaClass;
    private Consumer<Field> resultConsumer;

    public EnumSelectContent(Class<?> javaClass, Consumer<Field> resultConsumer) {
        this.javaClass = javaClass;
        this.resultConsumer = resultConsumer;
    }

    public Class<?> getJavaClass() {
        return javaClass;
    }

    public Consumer<Field> getResultConsumer() {
        return resultConsumer;
    }

    @Override
    public Pane impl_createPeer(OpenedDialog dialog) {
        return new EnumSelectUI(this, dialog);
    }
}
