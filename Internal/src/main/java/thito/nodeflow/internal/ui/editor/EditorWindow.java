package thito.nodeflow.internal.ui.editor;

import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.input.*;
import thito.nodeflow.library.ui.*;

public class EditorWindow extends Window implements WindowHitTest {
    @Override
    protected Skin createSkin() {
        return new EditorSkin();
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



