package thito.nodeflow.library.ui;

import javafx.scene.image.*;

import java.io.*;

public class Theme {
    private File root;

    public Theme(File root) {
        this.root = root;
    }

    public File getRoot() {
        return root;
    }

    public Image loadImage(String path) {
        try (FileInputStream fileInputStream = new FileInputStream(new File(root, path))) {
            return new Image(fileInputStream);
        } catch (Throwable t) {
            return new WritableImage(0, 0);
        }
    }
}
