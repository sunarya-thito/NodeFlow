package thito.nodeflow.internal.project.module;

import javafx.scene.*;
import thito.nodeflow.library.resource.*;

public interface FileViewer {
    Resource getResource();
    FileModule getModule();
    Node getNode();
}
