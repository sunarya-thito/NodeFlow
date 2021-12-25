package thito.nodeflow;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import thito.nodeflow.profiler.ProfilerWindow;
import thito.nodeflow.task.TaskThread;
import thito.nodeflow.task.batch.Progress;
import thito.nodeflow.ui.StandardWindow;
import thito.nodeflow.ui.dashboard.ProjectHelper;
import thito.nodeflow.ui.docker.*;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DebugMode {

    public static class ThreadChecker {
        private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        public void run() {
            executorService.schedule(() -> {
                long time = System.currentTimeMillis();
                TaskThread.UI().schedule(() -> {
                    long elapsed = System.currentTimeMillis() - time;
                    System.out.println("Elapsed time for UI "+elapsed);
                    run();
                });
            }, 2, TimeUnit.SECONDS);
        }
    }

    public static void startDebugMode() {
        TaskThread.UI().schedule(() -> {
            StandardWindow standardWindow = new StandardWindow();
            DockerContext context = new DockerContext();
            context.setDockerWindowSupplier(() -> new DockerWin(context));
            DockerPane dockerPane = new DockerPane(context);
            fillIn(dockerPane.getRightTabs(), "Right");
            fillIn(dockerPane.getCenterTabs(), "Center");
            fillIn(dockerPane.getLeftTabs(), "Left");
            fillIn(dockerPane.getBottomTabs(), "Bottom");
            standardWindow.contentProperty().set(dockerPane);
            standardWindow.show();
//            standardWindow.progressProperty().set(new Progress(new SimpleStringProperty("Demo Progress"), new SimpleDoubleProperty(-1)));
//            new ProfilerWindow().show();
        });
    }

    public static class DockerWin extends StandardWindow implements DockerWindow {
        private DockerPane dockerPane;

        public DockerWin(DockerContext dockerContext) {
            dockerPane = new DockerPane(dockerContext);
            contentProperty().set(dockerPane);
            DockerWindow.autoClose(this);
        }

        @Override
        public void setPosition(double screenX, double screenY) {
            getStage().setX(screenX);
            getStage().setY(screenY);
        }

        @Override
        public DockerPane getDockerPane() {
            return dockerPane;
        }
    }

    static void fillIn(DockerTabPane pane, String text) {
        int max = new Random().nextInt(5) + 3;
        for (int i = 0; i < max; i++) {
            DockerTab tab = new DockerTab(pane.getContext());
            tab.titleProperty().set(text+" "+i);
            BorderPane px = new BorderPane();
            px.setBackground(new Background(new BackgroundFill(ProjectHelper.randomPastelColor(), null, null)));
//            tab.setCenter(px);
            pane.getTabList().add(tab);
        }
    }
}
