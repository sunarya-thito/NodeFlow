package thito.nodeflow.internal.ui;

import javafx.beans.property.*;
import thito.nodeflow.api.*;
import thito.nodeflow.api.resource.*;
import thito.nodeflow.api.ui.*;

import java.io.*;

public class ImageImpl implements Image {

    protected ObjectProperty<javafx.scene.image.Image> peerProperty = new SimpleObjectProperty<>();
    protected ImageImpl() {}

    public ImageImpl(InputStream inputStream) {
        if (inputStream != null) {
            peerProperty.set(new javafx.scene.image.Image(inputStream));
        }
    }

    public ImageImpl(ResourceFile file) {
        load(file);
    }

    protected void load(ResourceFile file) {
        try (InputStream inputStream = file.openInput()) {
            peerProperty.set(new javafx.scene.image.Image(inputStream));
        } catch (IOException e) {
            throw new ReportedError(e);
        }
    }

    @Override
    public ReadOnlyObjectProperty<javafx.scene.image.Image> impl_propertyPeer() {
        return peerProperty;
    }
}
