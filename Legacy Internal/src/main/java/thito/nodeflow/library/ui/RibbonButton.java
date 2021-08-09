package thito.nodeflow.library.ui;

import com.jfoenix.controls.*;
import javafx.beans.property.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;

public class RibbonButton extends JFXButton {
    private StringProperty text = new SimpleStringProperty();
    private ObjectProperty<Image> icon = new SimpleObjectProperty<>();
    public RibbonButton() {
        setGraphic(new RibbonPeer());
        getStyleClass().add("ribbon-button");
    }

    public String getInnerText() {
        return text.get();
    }

    public Image getIcon() {
        return icon.get();
    }

    public void setInnerText(String text) {
        this.text.set(text);
    }

    public void setIcon(Image icon) {
        this.icon.set(icon);
    }

    public StringProperty innerTextProperty() {
        return text;
    }

    public ObjectProperty<Image> iconProperty() {
        return icon;
    }

    public class RibbonPeer extends VBox {

        public RibbonPeer() {
            getStyleClass().add("ribbon-button-viewport");
            BetterImageView image = new BetterImageView();
            image.getStyleClass().add("ribbon-button-icon");
            image.imageProperty().bind(icon);
            Text text = new Text();
            text.getStyleClass().add("ribbon-button-text");
            text.textProperty().bind(RibbonButton.this.text);
            getChildren().addAll(image, text);
        }
    }
}
