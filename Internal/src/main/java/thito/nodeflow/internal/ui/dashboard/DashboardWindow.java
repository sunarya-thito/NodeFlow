package thito.nodeflow.internal.ui.dashboard;

import javafx.geometry.*;
import javafx.scene.input.*;
import javafx.stage.*;
import thito.nodeflow.internal.language.*;
import thito.nodeflow.internal.ui.Window;
import thito.nodeflow.internal.ui.*;

public class DashboardWindow extends Window implements WindowHitTest {

    private static DashboardWindow window;

    public static DashboardWindow getWindow() {
        return window;
    }

    public DashboardWindow() {
        window = this;
    }

    @Override
    protected void initializeWindow() {
        super.initializeWindow();
        titleProperty().bind(I18n.$("dashboard.title"));
    }

    @Override
    protected WindowHitTest createHitTest() {
        return this;
    }

    @Override
    protected Skin createSkin() {
        return new DashboardSkin();
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
