package thito.nodeflow.internal.ui;

import javafx.application.*;
import javafx.stage.*;

import java.io.*;

public class SkinTestViewer extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        SkinViewer viewer = new SkinViewer(new File("C:\\Users\\Thito\\IdeaProjects\\NodeFlow Software\\InternalLibrary\\src\\main\\resources\\Test.xml"));
        viewer.getStage().show();
    }
}
