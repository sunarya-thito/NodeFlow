package thito.nodeflow.library.ui.decoration.window;

import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.stage.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.library.binding.*;
import thito.nodeflow.library.ui.*;

public class WindowControl {

    private WindowBar bar;
    private HBox pane = new HBox();
    private ObservableList<Button> buttons = FXCollections.observableArrayList();

    public WindowControl(WindowBar bar) {
        this.bar = bar;
        Pseudos.install(pane, Pseudos.HOVERED, MouseEvent.MOUSE_ENTERED, MouseEvent.MOUSE_EXITED);
        Toolkit.style(pane, "window-control");
        MappedListBinding.bind(pane.getChildren(), buttons, Button::getPane);
        initDefaultButtons();
    }

    private void initDefaultButtons() {
        Button close = new Button("window-button-close");
        close.circle.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                bar.getBase().stage.fireEvent(new WindowEvent(bar.getBase().stage, WindowEvent.WINDOW_CLOSE_REQUEST));
            }
        });
        Button maximize = new Button("window-button-maximize");
        maximize.circle.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                bar.getBase().stage.setMaximized(!bar.getBase().stage.isMaximized());
            }
        });
        Button minimize = new Button("window-button-minimize");
        minimize.circle.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                bar.getBase().stage.setIconified(true);
            }
        });
        buttons.addAll(close, minimize, maximize);
    }

    public boolean preventWindowDrag(double x, double y) {
        for (Button button : buttons) {
            Circle circle = button.circle;
            Point2D local = circle.screenToLocal(x, y);
            if (local != null && circle.contains(local)) {
                return true;
            }
        }
        return false;
    }

    protected HBox getPane() {
        return pane;
    }

    public class Button {
        private BorderPane pane;
        private Circle circle;

        public Button(String name) {
            circle = new Circle();
            circle.setPickOnBounds(false);
            pane = new BorderPane(circle);
            pane.setPickOnBounds(false);
            BorderPane.setAlignment(circle, Pos.CENTER);
            Toolkit.style(circle, "window-button-icon");
            Toolkit.style(this.pane, "window-button-viewport");
            circle.setId(name);
            Pseudos.install(circle, Pseudos.HOVERED, MouseEvent.MOUSE_ENTERED, MouseEvent.MOUSE_EXITED);
        }

        protected BorderPane getPane() {
            return pane;
        }
    }
}
