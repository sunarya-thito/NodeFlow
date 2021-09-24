package thito.nodeflow.internal.ui;

import javafx.beans.property.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;

public class DialogWindow extends Window implements WindowHitTest {
    private BorderPane content;

    public ObjectProperty<Node> contentProperty() {
        return content.centerProperty();
    }

    @Override
    protected void initializeWindow() {
        super.initializeWindow();
        content = new BorderPane();
    }

    @Override
    public StringProperty titleProperty() {
        return super.titleProperty();
    }

    protected BorderPane getContent() {
        return content;
    }

    @Override
    public DialogWindowSkin getSkin() {
        return (DialogWindowSkin) super.getSkin();
    }

    @Override
    protected Skin createSkin() {
        return new DialogWindowSkin(this);
    }

    @Override
    protected WindowHitTest createHitTest() {
        return this;
    }

    @Override
    public HitTestAction testHit(int screenX, int screenY, MouseButton button) {
        DialogWindowSkin skin = getSkin();
        if (skin.root != null) {
            Point2D local = skin.root.screenToLocal(screenX, screenY);
            if (local != null) {
                Corner corner = UIHelper.getCorner(local.getX(), local.getY(), skin.root.getWidth(), skin.root.getHeight(), 5);
                if (corner == Corner.CENTER) {
                    Point2D captionLocal = skin.caption.screenToLocal(screenX, screenY);
                    if (skin.caption.contains(captionLocal)) {
                        for (Node child : skin.caption.getChildren()) {
                            if (child.getStyleClass().contains("not-caption") && child.contains(child.parentToLocal(captionLocal))) {
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
