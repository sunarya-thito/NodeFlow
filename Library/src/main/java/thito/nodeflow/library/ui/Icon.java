package thito.nodeflow.library.ui;

import javafx.beans.property.*;
import javafx.scene.image.*;

public class Icon {
    public static final Icon EMPTY = new Icon(new WritableImage(0, 0));

    private ObjectProperty<Image> image;

    public Icon(Image img) {
        this(new SimpleObjectProperty<>(img));
    }

    public Icon(ObjectProperty<Image> image) {
        this.image = image;
    }

    public ObjectProperty<Image> imageProperty() {
        return image;
    }
}
