package thito.nodeflow.ui;

import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Window;
import org.dockfx.DockNode;
import org.dockfx.DockPane;

public class StandardDockNode extends DockNode {
    private StandardWindow window;
    public StandardDockNode(Node contents, String title, Node graphic) {
        super(contents, title, graphic);
    }

    public StandardDockNode(Node contents, String title) {
        super(contents, title);
    }

    public StandardDockNode(Node contents) {
        super(contents);
    }

    @Override
    public void setFloating(boolean floating, Point2D translation) {
        if (floating && !this.isFloating()) {
            Point2D floatScene = this.localToScene(0.0D, 0.0D);
            Point2D floatScreen = this.localToScreen(0.0D, 0.0D);
            getDockTitleBar().setVisible(this.isCustomTitleBar());
            getDockTitleBar().setManaged(this.isCustomTitleBar());
            floatingProperty().set(true);
            this.applyCss();
            if (this.isDocked()) {
                this.undock();
            }
            window = new StandardWindow();
            window.titleProperty().bind(titleProperty());
            DockPane dockPane = getDockPane();
            if (dockPane != null && dockPane.getScene() != null && dockPane.getScene().getWindow() != null) {
                window.getStage().initOwner(dockPane.getScene().getWindow());
            }

            Point2D stagePosition;
            Window owner = window.getStage().getOwner();
            if (this.isDecorated() && owner != null) {
                stagePosition = floatScene.add(new Point2D(owner.getX(), owner.getY()));
            } else {
                stagePosition = floatScreen;
            }

            if (translation != null) {
                stagePosition = stagePosition.add(translation);
            }

            BorderPane borderPane = new BorderPane();
            borderPane.getStyleClass().add("dock-node-border");
            borderPane.setCenter(this);
            window.contentProperty().set(borderPane);
            Insets insetsDelta = borderPane.getInsets();
            double insetsWidth = insetsDelta.getLeft() + insetsDelta.getRight();
            double insetsHeight = insetsDelta.getTop() + insetsDelta.getBottom();
            window.getStage().setX(stagePosition.getX() - insetsDelta.getLeft());
            window.getStage().setY(stagePosition.getY() - insetsDelta.getTop());
            window.getStage().setMinWidth(borderPane.minWidth(this.getHeight()) + insetsWidth);
            window.getStage().setMinHeight(borderPane.minHeight(this.getWidth()) + insetsHeight);
            borderPane.setPrefSize(this.getWidth() + insetsWidth, this.getHeight() + insetsHeight);

            window.getStage().setResizable(this.isStageResizable());
            if (this.isStageResizable()) {
                window.getStage().addEventFilter(MouseEvent.MOUSE_PRESSED, this);
                window.getStage().addEventFilter(MouseEvent.MOUSE_MOVED, this);
                window.getStage().addEventFilter(MouseEvent.MOUSE_DRAGGED, this);
            }

            window.getStage().sizeToScene();
            window.show();
        } else if (!floating && this.isFloating()) {
            floatingProperty().set(false);
            window.getStage().removeEventFilter(MouseEvent.MOUSE_PRESSED, this);
            window.getStage().removeEventFilter(MouseEvent.MOUSE_MOVED, this);
            window.getStage().removeEventFilter(MouseEvent.MOUSE_DRAGGED, this);
            window.close();
            window = null;
        }
    }
}
