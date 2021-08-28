package thito.nodeflow.internal.ui;

import javafx.beans.property.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import thito.nodeflow.library.ui.*;

public class StandardWindow extends Window implements WindowHitTest {
    private BorderPane content;

    public ObjectProperty<Node> contentProperty() {
        return content.centerProperty();
    }

    @Override
    protected void initializeWindow() {
        super.initializeWindow();
        content = new BorderPane();
    }

    protected BorderPane getContent() {
        return content;
    }

    @Override
    public StandardWindowSkin getSkin() {
        return (StandardWindowSkin) super.getSkin();
    }

    @Override
    protected Skin createSkin() {
        return new StandardWindowSkin(this);
    }

    @Override
    protected WindowHitTest createHitTest() {
        return this;
    }

    @Override
    public HitTestAction testHit(int screenX, int screenY, MouseButton button) {
        StandardWindowSkin skin = getSkin();
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
