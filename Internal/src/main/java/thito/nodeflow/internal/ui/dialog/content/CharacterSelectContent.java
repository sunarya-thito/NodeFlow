package thito.nodeflow.internal.ui.dialog.content;

import javafx.scene.layout.*;
import thito.nodeflow.api.ui.dialog.*;
import thito.nodeflow.api.ui.dialog.content.*;
import thito.nodeflow.internal.ui.editor.*;

import java.util.function.*;

public class CharacterSelectContent implements DialogContent {
    private Consumer<Character> result;

    public CharacterSelectContent(Consumer<Character> result) {
        this.result = result;
    }

    public Consumer<Character> getResultConsumer() {
        return result;
    }

    @Override
    public Pane impl_createPeer(OpenedDialog dialog) {
        return new CharacterSelectUI(this, dialog);
    }
}
