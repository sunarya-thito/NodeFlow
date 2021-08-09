package thito.nodeflow.internal.ui.dialog.button;

import com.jfoenix.controls.*;
import javafx.beans.*;
import javafx.beans.value.*;
import javafx.scene.image.Image;
import thito.nodeflow.api.ui.*;
import thito.nodeflow.api.ui.dialog.button.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.library.ui.*;

public class TextButtonPeerImpl extends JFXButton implements TextButtonPeer {
    private TextDialogButton button;

    public TextButtonPeerImpl(TextDialogButton button) {
        this.button = button;
        Toolkit.style(this, "dialog-button");
        textProperty().bind(button.getLabel().stringBinding());
        Icon icon = button.getIcon();
        if (icon != null) {
            icon.impl_propertyPeer().addListener(new WeakChangeListener<>(this::updateImage));
        }
        disableProperty().bind(button.impl_disableProperty());
    }

    private void updateImage(Observable observable, Image old, Image img) {
        BetterImageView graphics = new BetterImageView(img);
        graphics.setFitMode(BetterImageView.FitMode.SCALE_FIT_MAX);
        setGraphic(graphics);
    }

    @Override
    public TextDialogButton getButton() {
        return button;
    }

}
