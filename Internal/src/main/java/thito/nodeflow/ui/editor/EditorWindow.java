package thito.nodeflow.ui.editor;

import javafx.beans.binding.Bindings;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import thito.nodeflow.binding.MappedBinding;
import thito.nodeflow.editor.Editor;
import thito.nodeflow.editor.EditorManager;
import thito.nodeflow.language.I18n;
import thito.nodeflow.task.TaskThread;
import thito.nodeflow.ui.*;
import thito.nodeflow.ui.dashboard.DashboardWindow;

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
        getStage().showingProperty().addListener((obs, old, val) -> {
            TaskThread.BG().schedule(() -> {
                if (val) {
                    EditorManager.getActiveEditors().add(editor);
                } else {
                    EditorManager.getActiveEditors().remove(editor);
                }
            });
        });
        getStage().focusedProperty().addListener((obs, old, val) -> {
            DashboardWindow.getWindow().getStage().toFront();
        });
    }

    public Editor getEditor() {
        return editor;
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



