package thito.nodeflow.internal.ui.dialog.content;

import com.jfoenix.controls.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import thito.nodeflow.api.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.ui.Pos;
import thito.nodeflow.api.ui.dialog.Dialog;
import thito.nodeflow.api.ui.dialog.*;
import thito.nodeflow.api.ui.dialog.content.*;
import thito.nodeflow.internal.Toolkit;
import thito.nodeflow.library.ui.*;

public abstract class AbstractTitledContent implements TitledContent {

    private I18nItem header;
    private Pos alignment;
    private Dialog.Type type;
    private Dialog.Level level;

    public AbstractTitledContent(Dialog.Type type, Dialog.Level level, I18nItem header, Pos alignment) {
        this.header = header;
        this.alignment = alignment;
        this.type = type;
        this.level = level;
    }

    @Override
    public Dialog.Type getType() {
        return type;
    }

    @Override
    public Dialog.Level getLevel() {
        return level;
    }

    @Override
    public void setType(Dialog.Type type) {
        this.type = type;
    }

    @Override
    public void setLevel(Dialog.Level level) {
        this.level = level;
    }

    @Override
    public Pane impl_createPeer(OpenedDialog dialog) {
        HBox boxPane = new HBox();
        boxPane.setFillHeight(true);
        boxPane.setSpacing(20);
        if (type == Dialog.Type.LOADING) {
            JFXSpinner spinner = new JFXSpinner();
            spinner.setMaxSize(55, 70);
            spinner.setMinSize(55, 70);
            boxPane.getChildren().add(spinner);
        } else {
            BetterImageView icon = new BetterImageView(
                    NodeFlow.getApplication().getResourceManager().getIcon(type.name().toLowerCase()+"_"+
                            level.name().toLowerCase())
            );
            icon.setFitMode(BetterImageView.FitMode.SCALE_FIT_MIN);
            icon.setAlignment(javafx.geometry.Pos.TOP_CENTER);
            icon.setMinHeight(70);
            icon.setMinWidth(55);
            boxPane.getChildren().add(icon);
        }
        boxPane.setPadding(new Insets(5, 0, 0, 0));
        Label titleLabel = new Label();
        thito.nodeflow.internal.Toolkit.style(titleLabel, "dialog-content-title");
        titleLabel.textProperty().bind(getHeader().stringBinding());
        titleLabel.setTextAlignment(Toolkit.posToTextAlignment(alignment));
        Region label = impl_createContentPeer();
        VBox panel = new VBox(titleLabel, label);
        boxPane.getChildren().add(panel);
        label.setMaxWidth(330);
        BorderPane.setAlignment(titleLabel, javafx.geometry.Pos.BOTTOM_LEFT);
        BorderPane.setAlignment(label, javafx.geometry.Pos.TOP_LEFT);
        BorderPane.setAlignment(panel, javafx.geometry.Pos.CENTER_LEFT);
        BorderPane.setMargin(label, new Insets(3, 0, 0, 0));
        BorderPane.setMargin(panel, new Insets(0, 0, 0, 20));
        return boxPane;
    }

    @Override
    public I18nItem getHeader() {
        return header;
    }

    @Override
    public void setHeader(I18nItem title) {
        header = title;
    }

    @Override
    public Pos getHeaderAlignment() {
        return alignment;
    }

    @Override
    public void setHeaderAlignment(Pos pos) {
        alignment = pos;
    }

}
