package thito.nodeflow.ui.editor;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import thito.nodeflow.language.I18n;
import thito.nodeflow.project.ProjectContext;
import thito.nodeflow.project.ProjectManager;
import thito.nodeflow.task.TaskThread;
import thito.nodeflow.ui.*;
import thito.nodeflow.ui.dashboard.DashboardWindow;
import thito.nodeflow.ui.dialog.Dialog;
import thito.nodeflow.ui.dialog.Dialogs;
import thito.nodeflow.ui.docker.DockerPane;
import thito.nodeflow.ui.docker.DockerWindow;

public class EditorWindow extends Window implements WindowHitTest, DockerWindow {

    private DockerPane dockerPane;
    private ProjectContext context;
    public EditorWindow(ProjectContext context, DockerPane dockerPane) {
        this.context = context;
        this.dockerPane = dockerPane;
        dockerPane.checkAutoCloseProperty().set(() -> {
            if (dockerPane.getCenterTabs().getTabList().isEmpty() &&
                    dockerPane.getLeftTabs().getTabList().isEmpty() &&
                    dockerPane.getRightTabs().getTabList().isEmpty() &&
                    dockerPane.getBottomTabs().getTabList().isEmpty()) {
                super.close();
            }
        });
    }

    @Override
    public void setPosition(double screenX, double screenY) {
        getStage().setX(screenX);
        getStage().setY(screenY);
    }

    public DockerPane getDockerPane() {
        return dockerPane;
    }

    public ProjectContext getContext() {
        return context;
    }

    @Override
    protected void initializeWindow() {
        super.initializeWindow();
        getStage().showingProperty().addListener((obs, old, val) -> {
            TaskThread.BG().schedule(() -> {
                if (val) {
                    context.getActiveWindows().add(this);
                } else {
                    context.getActiveWindows().remove(this);
                }
            });
        });
    }

    @Override
    public void close() {
        if (context.getActiveWindows().size() <= 1) {
            Dialogs.ask(Dialog.create().title(I18n.$("dialogs.close-project.title"))
                    .message(I18n.$("dialogs.close-project.message")).question(), result -> {
                if (result) {
                    context.getTaskQueue().executeBatch(
                            ProjectManager.getInstance().closeProject(context)
                    );
                }
            });
        } else {
            super.close();
        }
    }

    public void forceClose() {
        super.close();
    }

    @Override
    protected Skin createSkin() {
        return new EditorSkin(this);
    }

    @Override
    public EditorSkin getSkin() {
        return (EditorSkin) super.getSkin();
    }

    @Override
    protected WindowHitTest createHitTest() {
        return this;
    }

    @Override
    public HitTestAction testHit(int screenX, int screenY, MouseButton button) {
        EditorSkin skin = getSkin();
        if (skin.root != null) {
            Point2D local = skin.root.screenToLocal(screenX, screenY);
            if (local != null) {
                Corner corner = UIHelper.getCorner(local.getX(), local.getY(), skin.root.getWidth(), skin.root.getHeight(), 5);
                if (corner == Corner.CENTER) {
                    Point2D captionLocal = skin.caption.screenToLocal(screenX, screenY);
                    if (skin.caption.contains(captionLocal)) {
                        if (button == MouseButton.MIDDLE) {
                            return HitTestAction.MAXIMIZE_BUTTON;
                        }
                        for (Node child : skin.caption.getChildren()) {
                            if (child.contains(child.parentToLocal(captionLocal))) {
                                return HitTestAction.CLIENT;
                            }
                        }
                        return HitTestAction.CAPTION;
                    }
                }
                return corner.getHitTestAction();
            }
        }
        return HitTestAction.NOWHERE;
    }
}



