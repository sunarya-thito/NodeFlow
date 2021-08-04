package thito.nodeflow.internal.ui.popup;

import com.sandec.mdfx.*;
import javafx.animation.*;
import javafx.collections.*;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.effect.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.util.Duration;
import thito.nodeflow.api.task.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.internal.ui.*;
import thito.nodeflow.library.binding.*;
import thito.nodeflow.library.ui.decoration.popup.*;

import java.util.*;

public class NotificationPopup extends PopupBase {
    private static ObservableList<NotificationPopup> popups = FXCollections.observableArrayList();
    public static void initialize() {
        MappedListBinding.bind(popups, Screen.getScreens(), NotificationPopup::new);
    }

    private static long id;

    public static long requestId() {
        return id++;
    }

    public static void showNotification(String markdownText) {
        Task.runOnForeground("notification", () -> {
            long x = requestId();
            for (NotificationPopup popup : new ArrayList<>(popups)) {
                popup.push(popup.new Notification(x, markdownText));
            }
        });
    }

    private Screen screen;
    private Pane pane = new Pane();
    public NotificationPopup(Screen screen) {
        super(getInvisibleParent());
        this.screen = screen;
        pane.setPickOnBounds(false);
        Toolkit.style(pane, "notification-container");
        getStage().setAlwaysOnTop(true);
        getStage().getScene().setRoot(pane);
        getStage().setOnShown(event -> {
            updatePosition();
        });
        getStage().setWidth(300);
        getStage().setHeight(1);
        getStage().widthProperty().addListener(obs -> updatePosition());
        getStage().heightProperty().addListener(obs -> updatePosition());
        getStage().setOnCloseRequest(Event::consume);
        getStage().show();
    }

    private void updatePosition() {
        getStage().setX(screen.getVisualBounds().getMaxX() - getStage().getWidth());
        getStage().setY(StaticMenuBar.HEIGHT);
    }

    private Timeline timeline;
    private List<Runnable> onDone = new ArrayList<>();

    public Notification getById(long id) {
        for (int i = pane.getChildren().size() - 1; i >= 0; i--) {
            Node node = pane.getChildren().get(i);
            if (node instanceof Notification && ((Notification) node).id == id) {
                return (Notification) node;
            }
        }
        return null;
    }

    public void remove(Notification notification) {
        remove(notification, true);
    }

    public void remove(Notification notification, boolean recursive) {
        if (timeline != null) {
            onDone.add(() -> remove(notification));
            return;
        }
        if (recursive) {
            for (NotificationPopup popup : new ArrayList<>(popups)) {
                if (popup == this) continue;
                Notification x = popup.getById(notification.id);
                if (x != null) {
                    popup.remove(x, false);
                }
            }
        }
        double height = notification.getHeight();
        int index = pane.getChildren().indexOf(notification);
        pane.getChildren().remove(index);
        List<KeyValue> frames = new ArrayList<>();
        double targetHeight = 0;
        for (int i = pane.getChildren().size() - 1; i >= 0; i--) {
            Node node = pane.getChildren().get(i);
            if (node instanceof Notification) {
                Notification notif = (Notification) node;
                targetHeight += notif.getHeight();
                if (i < index) {
                    frames.add(new KeyValue(notif.layoutYProperty(), notif.getLayoutY() - height, Interpolator.EASE_OUT));
                }
            }
        }
        timeline = new Timeline(new KeyFrame(Duration.millis(300), frames.toArray(new KeyValue[0])));
        final double th = Math.min(screen.getVisualBounds().getHeight() - StaticMenuBar.HEIGHT, targetHeight + 40);
        timeline.setOnFinished(finished -> {
            getStage().setHeight(th);
            List<Runnable> done = new ArrayList<>(onDone);
            onDone.clear();
            for (Runnable rx : done) {
                rx.run();
            }
            timeline = null;
        });
        timeline.play();
    }

    public void push(Notification notification) {
        if (timeline != null) {
            onDone.add(() -> push(notification));
            return;
        }
        notification.layoutXProperty().bind(pane.widthProperty().subtract(notification.widthProperty()));
        pane.getChildren().add(notification);
        Task.runOnForegroundLater("notification-pending", () -> {
            double height = notification.getHeight();
            notification.setLayoutY(-height);
            List<KeyValue> frames = new ArrayList<>();
            double targetHeight = 0;
            for (Node node : pane.getChildren()) {
                if (node instanceof Notification) {
                    Notification notif = (Notification) node;
                    frames.add(new KeyValue(notif.layoutYProperty(), notif.getLayoutY() + height, Interpolator.EASE_OUT));
                    targetHeight += notif.getHeight();
                }
            }
            getStage().setHeight(Math.min(screen.getVisualBounds().getHeight() - StaticMenuBar.HEIGHT, targetHeight + 40));
            timeline = new Timeline(new KeyFrame(Duration.millis(300), frames.toArray(new KeyValue[0])));
            timeline.setOnFinished(finished -> {
                List<Runnable> done = new ArrayList<>(onDone);
                onDone.clear();
                for (Runnable rx : done) {
                    rx.run();
                }
                timeline = null;
            });
            timeline.play();
        }, thito.nodeflow.api.task.Duration.millis(100));
    }

    public class Notification extends StackPane {
        private ColorAdjust adjust = new ColorAdjust(0, 0, 0, 0);
        private long id;
        public Notification(long id, String markdown) {
            this.id = id;
            Toolkit.style(this, "notification");
            MDFXNode mdNode = new MDFXNode(markdown);
            mdNode.getStylesheets().clear();
            setMaxWidth(250);
            setMinWidth(250);
            setEffect(adjust);
            getChildren().add(mdNode);
            setPickOnBounds(false);

            // Click Animation
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.millis(200), new KeyValue(opacityProperty(), 0), new KeyValue(scaleXProperty(), 0.8, Interpolator.EASE_BOTH), new KeyValue(scaleYProperty(), 0.8, Interpolator.EASE_BOTH))
            );
            timeline.setOnFinished(event -> {
                remove(this);
            });

            setOnMouseClicked(event -> {
                if (NotificationPopup.this.timeline == null) {
                    timeline.play();
                }
            });
        }
    }
}
