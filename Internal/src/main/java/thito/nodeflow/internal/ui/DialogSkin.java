package thito.nodeflow.internal.ui;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public class DialogSkin extends Skin {

    @Component("message")
    Label message;
    @Component("button-pane")
    Pane buttonPane;
    @Component("icon")
    ImagePane iconView;

    private Dialog dialog;
    private Dialog.Handler handler;

    public DialogSkin(Dialog dialog, Dialog.Handler handler) {
        this.dialog = dialog;
        this.handler = handler;
    }

    @Override
    protected void onLayoutLoaded() {
        if (dialog.buttons != null) {
            for (DialogButton b : dialog.buttons) {
                Button button = new Button();
                button.textProperty().bind(b.label);
                button.setDefaultButton(b.defaultButton);
                button.setCancelButton(b.cancelButton);
                button.setMnemonicParsing(b.mnemonics);
                button.setOnAction(e -> {
                    if (b.exec != null) {
                        b.exec.run();
                    } else if (dialog.fallback != null) {
                        dialog.fallback.run();
                    }
                    handler.dispose();
                });
                buttonPane.getChildren().add(button);
            }
        }
        if (dialog.message != null) {
            message.textProperty().bind(dialog.message);
        }
        if (dialog.icon != null) {
            iconView.setImage(new Image(dialog.icon));
        } else {
            ((Pane) iconView.getParent()).getChildren().remove(iconView);
        }
    }
}
