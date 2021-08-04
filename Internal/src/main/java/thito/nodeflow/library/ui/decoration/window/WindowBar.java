package thito.nodeflow.library.ui.decoration.window;

import javafx.beans.binding.*;
import javafx.beans.value.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.library.ui.*;
import thito.nodeflow.platform.*;

public class WindowBar {

    private WindowBase base;
    private HBox pane = new HBox();

    private WindowTitle title;
    private WindowControl control;
    private ContextMenu menu;

    private NativeHandle nativeHandle;

    public WindowBar(WindowBase base) {
        Pseudos.install(pane, Pseudos.STAGE_FOCUSED, base.getStage().focusedProperty());
        this.base = base;
        this.title = new WindowTitle(this);
        control = new WindowControl(this);
        Toolkit.style(pane, "window-bar");
        HBox.setHgrow(title.getPane(), Priority.ALWAYS);
        pane.getChildren().addAll(control.getPane(), title.getPane());
        initializeTooltip();
        nativeHandle = NativeHandle.createBorderlessWindowHandler(this);
    }

    private void initializeTooltip() {
        menu = new ContextMenu();
        {
            MenuItem item = new MenuItem();
            item.setOnAction(event -> {
                base.getStage().setIconified(true);
            });
            item.textProperty().bind(I18n.$("context-menu-minimize").stringBinding());
            menu.getItems().add(item);
        }
        {
            MenuItem item = new MenuItem();
            item.setOnAction(event -> {
                base.getStage().setMaximized(!base.getStage().isMaximized());
            });
            item.textProperty().bind(Bindings.when(getBase().stage.maximizedProperty()).then((ObservableStringValue) I18n.$("context-menu-restore").stringBinding()).otherwise((ObservableStringValue) I18n.$("context-menu-maximize").stringBinding()));
            menu.getItems().add(item);
        }
        {
            MenuItem item = new MenuItem();
            item.setOnAction(event -> {
                base.getStage().fireEvent(new WindowEvent(base.getStage(), WindowEvent.WINDOW_CLOSE_REQUEST));
            });
            item.textProperty().bind(I18n.$("context-menu-close").stringBinding());
            menu.getItems().add(item);
        }
    }

    public WindowControl getControl() {
        return control;
    }

    public WindowTitle getTitle() {
        return title;
    }

    public void showContextMenu(double x, double y) {
        menu.show(base.stage, x, y);
    }

    public HBox getPane() {
        return pane;
    }

    public WindowBase getBase() {
        return base;
    }

}
