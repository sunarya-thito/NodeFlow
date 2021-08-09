package thito.nodeflow.library.ui.decoration.popup;

import com.sun.jna.platform.win32.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.stage.*;
import thito.nodeflow.internal.*;

public class PopupBase {
    private static Stage invisibleParent;

    public static Stage getInvisibleParent() {
        if (invisibleParent == null) {
            invisibleParent = new Stage(StageStyle.UTILITY);
            invisibleParent.setScene(new Scene(new Pane()));
            invisibleParent.setX(-100);
            invisibleParent.setY(-100);
            invisibleParent.setWidth(0);
            invisibleParent.setHeight(0);
            invisibleParent.setOpacity(0);
            invisibleParent.show();
            invisibleParent.toBack();
            WinDef.HWND hWnd = new WinDef.HWND(Toolkit.getWindowPointer(invisibleParent));
            User32.INSTANCE.ShowWindow(hWnd, User32.SW_HIDE);
        }
        return invisibleParent;
    }

    private Stage stage;
    private Scene scene;
    private StackPane root;

    public PopupBase(Window owner) {
        initializeInterface(owner);
    }

    private void initializeInterface(Window owner) {
        stage = new Stage(StageStyle.TRANSPARENT);
        stage.initOwner(owner);
        stage.setScene(scene = new Scene(root = new StackPane()));
        root.setBackground(new Background(new BackgroundFill(null, null, null)));
        Toolkit.style(root, "popup-root");
        scene.setFill(Color.TRANSPARENT);
        stage.setResizable(false);
    }

    public StackPane getRoot() {
        return root;
    }

    public Stage getStage() {
        return stage;
    }

    public void show(double x, double y) {
        stage.setX(x);
        stage.setY(y);
        stage.show();
    }

    public void hide() {
        stage.hide();
    }

}
