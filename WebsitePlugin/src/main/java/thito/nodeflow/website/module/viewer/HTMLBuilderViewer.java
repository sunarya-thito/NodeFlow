package thito.nodeflow.website.module.viewer;

import javafx.collections.*;
import javafx.scene.*;
import javafx.scene.control.*;
import thito.nodeflow.internal.project.*;
import thito.nodeflow.internal.project.module.*;
import thito.nodeflow.internal.resource.*;

public class HTMLBuilderViewer implements FileViewer {
    @Override
    public Project getProject() {
        return null;
    }

    @Override
    public Resource getResource() {
        return null;
    }

    @Override
    public FileModule getModule() {
        return null;
    }

    @Override
    public Node getNode() {
        return null;
    }

    @Override
    public ObservableList<Menu> getAdditionalMenus() {
        return null;
    }

    @Override
    public void reload(byte[] data) {

    }
}
