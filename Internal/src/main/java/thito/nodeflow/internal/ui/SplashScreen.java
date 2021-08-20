package thito.nodeflow.internal.ui;

import javafx.beans.property.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.stage.*;
import javafx.util.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.library.task.*;
import thito.nodeflow.library.ui.Skin;
import thito.nodeflow.library.ui.*;

public class SplashScreen {
    ImageView logo = new ImageView();
    Label version = new Label();
    HBox container = new HBox(logo, version);
    Pane progressTrack = new Pane();
    Pane progressBar = new Pane(progressTrack);
    Label progressText = new Label();
    StackPane stackPane = new StackPane(progressBar, progressText);
    AnchorPane root = new AnchorPane(container, stackPane);
    Stage stage;
    BorderPane pane = new BorderPane(root);
    Scene scene = new Scene(pane, 600, 350, false, SceneAntialiasing.BALANCED);
    DoubleProperty progress = new SimpleDoubleProperty();

    private ScheduledTask task;

    public SplashScreen() {
        scene.setFill(Color.TRANSPARENT);
        pane.setEffect(new DropShadow(20, Color.color(0, 0, 0, 0.7)));
        pane.setPadding(new Insets(20));
        pane.setBackground(Background.EMPTY);
        progressText.setText("TEST LABEL PROGRESS");
        progressText.setTextFill(Color.WHITE);
        progressText.setFont(Font.loadFont("rsrc:Fonts/Roboto-Regular.ttf", 10));
        progressText.setPadding(new Insets(4));
        progressTrack.setBackground(new Background(new BackgroundFill(Color.web("#ED8936"), null, null)));
        progressTrack.prefHeightProperty().bind(progressBar.heightProperty());
        progressTrack.prefWidthProperty().bind(progressBar.widthProperty().multiply(progress));
        progressBar.setBackground(new Background(new BackgroundFill(Color.web("#1A202C"), null, null)));
        AnchorPane.setLeftAnchor(container, 35.);
        AnchorPane.setBottomAnchor(container, 30.);
        AnchorPane.setBottomAnchor(stackPane, 0.);
        AnchorPane.setLeftAnchor(stackPane, 0.);
        AnchorPane.setRightAnchor(stackPane, 0.);
        progressBar.setMaxHeight(Integer.MAX_VALUE);
        progressBar.setMaxWidth(Integer.MAX_VALUE);
        stackPane.setPrefHeight(10);
        container.setSpacing(10);
        container.setAlignment(Pos.BOTTOM_LEFT);
        version.setFont(Font.loadFont("rsrc:Fonts/AXIS.otf", 35));
        version.setTextFill(Color.color(1, 1, 1, 0.8));
        version.setTranslateY(5);
        stage = new Stage(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.setResizable(false);
        version.setText(Version.getCurrentVersion().getVersion());
        logo.setImage(new Image("rsrc:Images/NodeflowLogoWhite.png"));
        updateBackground();
    }

    int xOffset, yOffset;

    public Stage getStage() {
        return stage;
    }

    public void show() {
        task = TaskThread.UI().schedule(() -> {
            xOffset++;
            yOffset++;
            progress.set(Math.min(1, progress.get() + 0.002));
            updateBackground();
        }, Duration.millis(16), Duration.millis(16));
        stage.show();
        stage.toFront();
    }

    public void close() {
        stage.close();
        task.cancel();
    }

    void updateBackground() {
        root.setBackground(new Background(new BackgroundImage(new Image("rsrc:Images/Splash.png"), BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT,
                new BackgroundPosition(Side.LEFT, xOffset, false, Side.TOP, yOffset, false), BackgroundSize.DEFAULT)));
    }
}
