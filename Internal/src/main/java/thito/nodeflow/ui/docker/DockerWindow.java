package thito.nodeflow.ui.docker;

public interface DockerWindow {
    static void autoClose(DockerWindow dockerWindow) {
        DockerPane dockerPane = dockerWindow.getDockerPane();
        dockerPane.checkAutoCloseProperty().set(() -> {
            if (dockerPane.getLeftTabs().getTabList().isEmpty() &&
            dockerPane.getCenterTabs().getTabList().isEmpty() &&
            dockerPane.getRightTabs().getTabList().isEmpty() &&
            dockerPane.getBottomTabs().getTabList().isEmpty()) {
                dockerWindow.close();
            }
        });
    }
    DockerPane getDockerPane();
    void close();
    void show();
    void setPosition(double screenX, double screenY);
}
