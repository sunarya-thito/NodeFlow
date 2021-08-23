package thito.nodeflow.internal.ui.editor;

import javafx.beans.binding.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.input.*;
import thito.nodeflow.internal.ui.dashboard.*;
import thito.nodeflow.library.binding.*;
import thito.nodeflow.library.language.*;
import thito.nodeflow.library.ui.*;

public class EditorWindow extends Window implements WindowHitTest {
    private Editor editor;

    public EditorWindow(Editor editor) {
        this.editor = editor;
        titleProperty().bind(Bindings.when(editor.projectProperty().isNull()).then(I18n.$("editor.title-no-project"))
                .otherwise(I18n.$("editor.title").format(MappedBinding.map(editor.projectProperty(), project -> project == null ? null : project.getProperties().getName()))));
    }

    @Override
    protected void initializeWindow() {
        super.initializeWindow();
        getStage().focusedProperty().addListener((obs, old, val) -> {
            DashboardWindow.getWindow().getStage().toFront();
        });
    }

    public Editor getEditor() {
        return editor;
    }

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



