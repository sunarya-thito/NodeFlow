package thito.nodeflow.internal.project;

import javafx.beans.property.*;
import javafx.scene.image.*;

public class Tag {
    private ObjectProperty<Image> icon = new SimpleObjectProperty<>();
    private StringProperty name = new SimpleStringProperty();

    public Tag() {
        name.set("Test");
        icon.set(new Image("rsrc:Themes/Dark/Icons/SpigotLogo.png"));
    }

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
