package thito.nodeflow.internal.ui.dialog;

import javafx.animation.*;
import javafx.scene.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import thito.nodeflow.api.*;
import thito.nodeflow.api.task.*;
import thito.nodeflow.api.ui.*;
import thito.nodeflow.api.ui.action.*;
import thito.nodeflow.api.ui.dialog.*;
import thito.nodeflow.api.ui.dialog.button.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.internal.ui.*;
import thito.nodeflow.library.ui.decoration.dialog.*;


public class OpenedDialogImpl extends DialogBase implements OpenedDialog {

    private Dialog dialog;
    private CompletableFutureSupplier<DialogButton> button = FutureSupplier.createCompletable();
    private Timeline in, out;
    private Window owner;
    private DialogPeer peer;

    public OpenedDialogImpl(Dialog dialog, Window owner) {
        super(owner == null ? null : owner.impl_getPeer());
        this.dialog = dialog;
        this.owner = owner;
        initializeInterface();
        show();
    }

    @Override
    public Window getOwner() {
        return owner;
    }

    private double offsetX, offsetY;
    private void initializeInterface() {
        peer = new DialogPeer(this);
        peer.getViewport().setOnMousePressed(event -> {
            offsetX = event.getScreenX() - getStage().getX();
            offsetY = event.getScreenY() - getStage().getY();
        });

        peer.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.F5) {
                NodeFlow.getApplication().getUIManager().applyTheme(NodeFlow.getApplication().getUIManager().getTheme());
            }
        });

        peer.getViewport().setOnMouseDragged(event -> {
            double targetX = event.getScreenX() - offsetX;
            double targetY = event.getScreenY() - offsetY;
            getStage().setX(targetX);
            getStage().setY(targetY);
        });

        peer.getContent().setCenter(dialog.getContent().impl_createPeer(this));
        peer.setOpacity(0);
        peer.getViewport().setScaleX(0.5);
        peer.getViewport().setScaleY(0.5);

        peer.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));

        LayoutDebugger debugger = new LayoutDebugger(getStage());
        debugger.setNode(peer);
        StackPane stackPane = new StackPane(peer, debugger);
        stackPane.setBackground(Background.EMPTY);
        Scene scene = new Scene(stackPane);
        scene.setFill(Color.TRANSPARENT);
        getStage().setScene(scene);

        in = new Timeline(new KeyFrame(Duration.seconds(0.1),
                new KeyValue(peer.getViewport().scaleXProperty(), 1, Interpolator.EASE_BOTH),
                new KeyValue(peer.getViewport().scaleYProperty(), 1, Interpolator.EASE_BOTH),
                new KeyValue(peer.opacityProperty(), 1)
        ));
        out = new Timeline(new KeyFrame(Duration.seconds(0.15),
                new KeyValue(peer.opacityProperty(), 0),
                new KeyValue(peer.getViewport().scaleXProperty(), 0.8, Interpolator.EASE_BOTH),
                new KeyValue(peer.getViewport().scaleYProperty(), 0.8, Interpolator.EASE_BOTH)
        ));
        getStage().setOnCloseRequest(event -> {
            close(null);
        });
        out.setOnFinished(event -> {
            getStage().close();
        });

        scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.F6) {
                if (debugger.isEnabled()) {
                    debugger.disable();
                } else {
                    debugger.enable();
                }
            } else if (event.getCode() == KeyCode.F5) {
                reloadCSS();
            } else if (event.getCode() == KeyCode.F7) {
                debugger.setHold(!debugger.isHold());
            }
        });
    }
    private void reloadCSS() {
        NodeFlow.getApplication().getUIManager().applyTheme(NodeFlow.getApplication().getUIManager().getTheme());
    }

    protected void dispatchClick(DialogButton button, MouseEvent event) {
        ClickAction action = new ClickAction(event.getClickCount(), _convert(event.getButton()), this, button);
        button.dispatchClick(action);
    }

    private ClickAction.MouseButton _convert(MouseButton button) {
        if (button == MouseButton.MIDDLE) {
            return ClickAction.MouseButton.MIDDLE;
        }
        if (button == MouseButton.SECONDARY) {
            return ClickAction.MouseButton.RIGHT;
        }
        return ClickAction.MouseButton.LEFT;
    }

    @Override
    public Dialog getDialog() {
        return dialog;
    }

    @Override
    public void close(DialogButton button) {
        this.button.complete(button);
        dialog.getOpenedDialogs().remove(peer);
        hide();
    }

    @Override
    public FutureSupplier<DialogButton> getActor() {
        return button;
    }

    public void show() {
        ((UIManagerImpl) NodeFlow.getApplication().getUIManager()).initDialog(this);
        out.stop();
        in.stop();
        getStage().show();
        in.play();
    }

    public void hide() {
        ((UIManagerImpl) NodeFlow.getApplication().getUIManager()).disposeDialog(this);
        in.stop();
        out.stop();
        out.play();
    }

}
