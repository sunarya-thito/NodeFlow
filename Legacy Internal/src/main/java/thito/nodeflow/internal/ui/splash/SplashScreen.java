package thito.nodeflow.internal.ui.splash;

import javafx.animation.*;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.event.*;
import javafx.geometry.Pos;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.*;
import javafx.util.Duration;
import thito.nodeflow.api.*;
import thito.nodeflow.api.bundle.*;
import thito.nodeflow.api.task.*;
import thito.nodeflow.api.ui.*;
import thito.nodeflow.internal.Toolkit;
import thito.nodeflow.internal.*;
import thito.nodeflow.internal.bundle.java.oracle.*;
import thito.nodeflow.internal.ui.*;
import thito.nodeflow.library.ui.*;

import java.util.*;

public class SplashScreen {
    public static class SplashScreenStage extends Stage implements ExceptionalStage {
        public SplashScreenStage() {
            super(StageStyle.TRANSPARENT);
        }
    }
    private BorderPane shadowHolder = new BorderPane();
    private BorderPane shadow = new BorderPane();
    private Stage stage = new SplashScreenStage();
    private StackPane layers = new StackPane();
    private AnchorPane progressHolder = new AnchorPane();
    private Scene scene = new Scene(shadowHolder);
    private ImageView frontClip = new ImageView();
    private Pane dots = new Pane();
    private Label nodeFlow = new Label("NODEFLOW");
    private AlignedPane logoHolder = new AlignedPane();
    private Timeline timeline = new Timeline(new KeyFrame(Duration.millis(16), this::tick));
    private Label activity = new Label();
    private HBox progressBar = new HBox();
    private VBox activityBar = new VBox(activity);
    private IntegerProperty progress = new SimpleIntegerProperty();
    private Pane glassLayer = new Pane();

    public SplashScreen() {
        Icon icon = NodeFlow.getApplication().getResourceManager().getIcon("favicon");
        icon.impl_propertyPeer().addListener((obs, old, val) -> {
            if (val != null) {
                stage.getIcons().setAll(val);
            }
        });
        stage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, event -> {
            System.exit(0);
        });
        stage.getIcons().setAll(icon.impl_propertyPeer().get());
        initializeInterface();
        initializeProgressBar();
        initializeAnimation();
        Task.runOnBackground("initialize-splash-task", () -> {
            startTask();
        });
    }

    public void startTask() {
        BundleProperties JRE = new JREBundleProperties();
        JRE.loadBundle();
        NodeFlowImpl.getInstance().loadBundles();
        NodeFlowImpl.getInstance().loadFacets();
        finish();
    }

    private void initializeProgressBar() {
        progressBar.setFillHeight(true);
        progressBar.setSpacing(5);
        progressBar.setPadding(new Insets(0, 0, 0, 0));
        double max = 10;
        for (int i = 0; i < max; i++) {
            Pane rectangle = new Pane();
            HBox.setHgrow(rectangle, Priority.ALWAYS);
            rectangle.backgroundProperty().bind(Bindings.when(progress.greaterThan(i * (100 / max))).then(
                    new Background(new BackgroundFill(Color.web("#85FFDA", 0.75), null, null))
            ).otherwise(
                    new Background(new BackgroundFill(Color.web("#37686C", 0.5), null, null))
            ));
            rectangle.effectProperty().bind(Bindings.when(progress.greaterThan(i * (100 / max))).then(
                    new DropShadow(15, Color.web("#85FFDA"))
            ).otherwise((DropShadow) null));
            progressBar.getChildren().add(rectangle);
        }
    }

    public void done() {
        stage.close();
//        if (NodeFlow.getApplication().getSettings().getValue(ApplicationSettings.SHOW_INTRO)) {
//            try {
//                new IntroPlayer(() -> {
//                    UIManagerImpl.getInstance().getWindowsManager().getLauncher().show();
//                });
//                return;
//            } catch (URISyntaxException e) {
//            }
//            NodeFlow.getApplication().getSettings().get(ApplicationSettings.SHOW_INTRO).setValue(false);
//        }
        UIManagerImpl.getInstance().getWindowsManager().getLauncher().show();
    }

    private void initializeInterface() {
        activity.setTextFill(Color.WHITE);
        activity.setFont(Font.font(15));
        shadowHolder.setScaleX(0.8);
        shadowHolder.setScaleY(0.8);
        AnchorPane.setBottomAnchor(progressBar, 15d);
        AnchorPane.setLeftAnchor(progressBar, 30d);
        AnchorPane.setRightAnchor(progressBar, 30d);
        AnchorPane.setBottomAnchor(activityBar, 30d);
        AnchorPane.setLeftAnchor(activityBar, 30d);
        AnchorPane.setRightAnchor(activityBar, 30d);
        progressBar.setMinHeight(8);
        progressHolder.getChildren().addAll(activityBar, progressBar);
        shadow.setCenter(layers);
        shadowHolder.setCenter(shadow);
        shadow.setEffect(new DropShadow(40, Color.color(0, 0, 0, 0.5)));
        shadow.setPadding(new Insets(20));
        shadow.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
        shadowHolder.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
        nodeFlow.setTextFill(Color.WHITE);
        nodeFlow.setFont(Font.font("AXIS Extra Bold", 70));
        Label label = new Label(NodeFlow.getApplication().getVersion().toString());
        label.setFont(Font.font("AXIS Extra Bold", 20));
        label.setTextFill(Color.color(1, 1, 1, 0.5));
        FlowPane versionPane = new FlowPane(label);
        versionPane.setAlignment(Pos.CENTER_RIGHT);
        VBox boxLogo = new VBox(nodeFlow, versionPane);
        boxLogo.setSpacing(-20);
        logoHolder.getChildren().add(boxLogo);
        logoHolder.setAlignment(Pos.CENTER);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.requestFocus();
        layers.setBackground(new Background(new BackgroundFill(Color.web("#18203B"), null, null)));
        layers.setMinSize(572, 385);
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(layers.widthProperty());
        clip.heightProperty().bind(layers.heightProperty());
        clip.setArcHeight(30);
        clip.arcWidthProperty().bind(clip.arcHeightProperty());
        layers.setClip(clip);
        frontClip.setImage(NodeFlow.getApplication().getResourceManager().getImage("splash/SplashClip2").impl_propertyPeer().get());
        glassLayer.setBackground(new Background(new BackgroundFill(
                new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.color(0, 1, 1, 0.1)),
                        new Stop(1, Color.color(0, 1, 1, 0))
                        )
        , null, null),
                new BackgroundFill(new RadialGradient(0, 0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.color(0, 1, 1, 0.2)),
                        new Stop(1, Color.color(0, 1, 1, 0))
                        ), null, null)));
        layers.getChildren().addAll(dots, frontClip, glassLayer, logoHolder, progressHolder);
    }

    private void initializeAnimation() {
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void finish() {
        Task.runOnForeground("splash-progress", () -> {
            timeline.stop();
            done();
        });
    }

    private void center() {
        Toolkit.centerOnScreen(stage);
    }

    private int tickTime = 0;
    private void tick(ActionEvent e) {
        int pg = Math.min(100, Math.max(0, (int) (LocalProgress.get() * 100)));
        activity.setText(LocalProgress.getActivity());
        progress.set(pg);
//        if (tickTime % 5 == 0) {
////            progress.set(progress.get() + random.nextInt(4));
//            progress.set(progress.get() + 5);
//            if (progress.get() >= 100) {
//                timeline.stop();
//                done();
//                return;
//            }
//        }
        int total = (int) (dots.getWidth() / 6);
        if (total > 0 && tickTime % total == 0) {
            pushDot();
        }
        for (Node child : new ArrayList<>(dots.getChildren())) {
            if (child instanceof Circle) {
                DoubleProperty radius = ((Circle) child).radiusProperty();
                if (radius.get() > 400) {
                    DoubleProperty opacity = child.opacityProperty();
                    if (opacity.get() > 0) {
                        opacity.set(opacity.get() - 0.08);
                    } else {
                        dots.getChildren().remove(child);
                    }
                }
                radius.set(radius.get() + 4);
            }
        }
        tickTime++;
    }

    private void pushDot() {
        Circle circle = new Circle();
//        circle.setLayoutX(random.nextDouble() * dots.getWidth());
//        circle.setLayoutY(random.nextDouble() * dots.getHeight());
        circle.setLayoutX(dots.getWidth() / 2);
        circle.setLayoutY(dots.getHeight() / 2);
        circle.setFill(new RadialGradient(0, 0, 0.5, 0.5, 1.4, true, CycleMethod.NO_CYCLE, new Stop(0.2, Color.web("#58CB92", 0)), new Stop(1, Color.web("#58CB92"))));
        dots.getChildren().add(circle);
    }

    public void show() {
        stage.show();
        center();
    }

}
