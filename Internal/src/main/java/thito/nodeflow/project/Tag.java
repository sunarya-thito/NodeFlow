package thito.nodeflow.project;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

public class Tag {
    private ObjectProperty<Image> icon = new SimpleObjectProperty<>();
    private StringProperty name = new SimpleStringProperty();

    public Tag(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public ObjectProperty<Image> iconProperty() {
        return icon;
    }
}
