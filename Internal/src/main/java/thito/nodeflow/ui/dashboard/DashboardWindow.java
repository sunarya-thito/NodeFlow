package thito.nodeflow.ui.dashboard;

import javafx.geometry.*;
import javafx.scene.input.*;
import javafx.stage.*;
import thito.nodeflow.NodeFlow;
import thito.nodeflow.language.I18n;
import thito.nodeflow.project.ProjectManager;
import thito.nodeflow.project.ProjectProperties;
import thito.nodeflow.task.TaskThread;
import thito.nodeflow.task.batch.TaskQueue;
import thito.nodeflow.ui.*;
import thito.nodeflow.ui.Window;
import thito.nodeflow.ui.dialog.Dialog;
import thito.nodeflow.ui.dialog.Dialogs;

public class DashboardWindow extends Window implements WindowHitTest {

    private static DashboardWindow window;

    public static DashboardWindow getWindow() {
        return window;
    }

    private TaskQueue taskQueue;
    public DashboardWindow() {
        window = this;
    }

    @Override
    protected void initializeWindow() {
        super.initializeWindow();
        titleProperty().bind(I18n.$("dashboard.title"));
        taskQueue = new TaskQueue();
        progressProperty().bind(taskQueue.progressProperty());
    }

    public TaskQueue getTaskQueue() {
        return taskQueue;
    }

    public void openProject(ProjectProperties projectProperties) {
        taskQueue.executeBatch(ProjectManager.getInstance().openProject(projectProperties).execute(TaskThread.UI(), progress -> {
            getStage().close();
        }));
    }

    @Override
    public void close() {
        super.close();
        NodeFlow.getInstance().shutdown();
    }

    @Override
    protected WindowHitTest createHitTest() {
        return this;
    }

    @Override
    protected Skin createSkin() {
        return new DashboardSkin(this);
    }

    @Override
    public HitTestAction testHit(int screenX, int screenY, MouseButton button) {
        Stage stage = getStage();
        Point2D local = stage.getScene().getRoot().screenToLocal(screenX, screenY);
        DashboardSkin skin = (DashboardSkin) getSkin();
        if (local.getX() >= 0 && local.getY() >= 0 && local.getX() < stage.getWidth() && local.getY() < stage.getHeight()) {
            if (skin.viewport != null) {
                Point2D viewport = skin.viewport.screenToLocal(screenX, screenY);
                if (viewport != null) {
                    Corner corner = UIHelper.getCorner(viewport.getX(), viewport.getY(), skin.viewport.getWidth(), skin.viewport.getHeight(), 5);
                    if (corner == Corner.CENTER) {
                        if (skin.button != null) {
                            Point2D localButton = skin.button.screenToLocal(screenX, screenY);
                            if (localButton != null && skin.button.contains(localButton)) {
                                return HitTestAction.CLIENT;
                            }
                        }
                        if (skin.header != null) {
                            Point2D localHeader = skin.header.screenToLocal(screenX, screenY);
                            if (localHeader != null && skin.header.contains(localHeader)) {
                                return HitTestAction.CAPTION;
                            }
                        }
                    }
                    return corner.getHitTestAction();
                }
            }
        }
        return HitTestAction.NOWHERE;
    }
}
