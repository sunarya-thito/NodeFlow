package thito.nodeflow.api.ui;

public interface Theme {
    String getName();

    String getIconDirectoryPath();

    String[] getCSSPaths(Window window);
}
