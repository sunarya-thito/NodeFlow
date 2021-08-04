package thito.nodeflow.installer;

import javafx.application.*;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.media.*;
import javafx.scene.paint.Color;
import javafx.stage.*;
import jfxtras.styles.jmetro.*;
import thito.nodeflow.installer.wizard.*;

import java.awt.*;
import java.io.*;
import java.util.concurrent.*;

public class Main extends Application {

    public static int EXIT_CODE = 0;
    public static final Executor THREAD_POOL = Executors.newSingleThreadExecutor();
    public static File INSTALLATION_DIR = new File("").getAbsoluteFile();
    public static boolean RUN_ON_FINISHED = true;

    public static void main(String[] args) {
        if (!new File(INSTALLATION_DIR, "NodeFlow.exe").exists()) {
            INSTALLATION_DIR = new File(INSTALLATION_DIR, "NodeFlow").getAbsoluteFile();
        }
        System.setProperty("prism.lcdtext", "false");
        launch(args);
    }

    private Wizard[] steps;
    private static IntegerProperty currentStep = new SimpleIntegerProperty(-1);

    public static void invokeNextStep() {
        currentStep.set(currentStep.get() + 1);
    }

    public Image generateImage(double red, double green, double blue, double opacity) {
        WritableImage img = new WritableImage(1, 1);
        PixelWriter pw = img.getPixelWriter();

        Color color = Color.color(red, green, blue, opacity);
        pw.setColor(0, 0, color);
        return img ;
    }

    public void attemptClose(Stage owner) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(owner);
        new JMetro(dialog.getDialogPane(), Style.LIGHT);
        ((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons().add(generateImage(1, 1, 1, 1));
        //        dialog.setTitle("Cancel Installation");
        ButtonType yes = new ButtonType("_Yes", ButtonBar.ButtonData.YES);
        ButtonType no = new ButtonType("_No", ButtonBar.ButtonData.NO);
        dialog.getDialogPane().getButtonTypes().addAll(no, yes);
        dialog.getDialogPane().setContentText("Are you sure you want to cancel this installation?");
        dialog.showAndWait().ifPresent(result -> {
            if (result == yes) {
                System.exit(1602);
            }
        });
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        steps = new Wizard[] {
                new Welcome(),
                new SelectDirectory(),
                new Installing(),
                new Finish()
        };
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            attemptClose(primaryStage);
        });
        primaryStage.setTitle("NodeFlow Setup");
        primaryStage.getIcons().add(new Image(getClass().getClassLoader().getResource("favicon.png").toURI().toString()));
        BorderPane borderPane = new BorderPane();
        MediaPlayer player = new MediaPlayer(new Media(getClass().getClassLoader().getResource("banner.mp4").toURI().toString()));
        player.setCycleCount(-1);
        player.play();
        borderPane.setLeft(new MediaView(player));
        borderPane.setMinWidth(500);
        borderPane.setMinHeight(350);

        // Wizard Panel
        BorderPane wrapper = new BorderPane();

        VBox header = new VBox();
        header.getStyleClass().add("header-container");
        Label headerText = new Label("Welcome");
        headerText.getStyleClass().addAll("header-wizard");
        header.getChildren().addAll(headerText, new Separator());
        wrapper.setTop(header);

        Button next = new Button("Next (_N)");
        next.setOnAction(event -> {
            currentStep.set(currentStep.get() + 1);
        });
        Button prev = new Button("Back (_B)");
        prev.setOnAction(event -> {
            currentStep.set(currentStep.get() - 1);
        });
        Button cancel = new Button("Cancel (_C)");
        cancel.setOnAction(event -> {
            if (currentStep.get() == steps.length - 1) {
                if (RUN_ON_FINISHED && EXIT_CODE == 0) {
                    try {
                        Desktop.getDesktop().open(INSTALLATION_DIR);
                    } catch (IOException e) {
                    }
                }
                System.exit(EXIT_CODE);
            } else {
                attemptClose(primaryStage);
            }
        });
        cancel.getStyleClass().add("cancel");
        BorderPane cancelWrapper = new BorderPane(cancel);
        cancelWrapper.setPadding(new Insets(0, 0, 0, 10));
        HBox buttons = new HBox(prev, next, cancelWrapper);
        buttons.getStyleClass().add("buttons");
        VBox buttonsContainer = new VBox(new Separator(), buttons);
        buttonsContainer.getStyleClass().add("buttons-container");
        wrapper.setBottom(buttonsContainer);

        currentStep.addListener((obs, old, val) -> {
            if (steps != null) {
                if (old != null && old.intValue() >= 0 && old.intValue() < steps.length) {
                    Wizard oldStep = steps[old.intValue()];
                    if (oldStep != null) {
                        oldStep.activeProperty().set(false);
                    }
                }
                Wizard step = steps[val.intValue()];
                if (step != null) {
                    step.activeProperty().set(true);
                    wrapper.setCenter(step);
                    headerText.textProperty().bind(step.titleProperty());
                    next.disableProperty().bind(step.disableNextProperty().or(currentStep.greaterThanOrEqualTo(steps.length - 1)));
                    prev.disableProperty().bind(step.disableBackProperty().or(currentStep.lessThan(1)));
                    prev.textProperty().bind(step.backLabelProperty());
                    next.textProperty().bind(step.nextLabelProperty());
                    cancel.textProperty().bind(step.cancelLabelProperty());
                    cancel.disableProperty().bind(step.cancellableProperty().not());
                    return;
                }
            }
            headerText.textProperty().unbind();
            headerText.textProperty().set("?");
            prev.textProperty().unbind();
            next.textProperty().unbind();
            cancel.textProperty().unbind();
            next.disableProperty().unbind();
            prev.disableProperty().unbind();
            cancel.disableProperty().unbind();
            next.setDisable(true);
            prev.setDisable(true);
            cancel.setDisable(true);
        });

        borderPane.setCenter(wrapper);

        Scene scene = new Scene(borderPane);
        new JMetro(scene, Style.LIGHT);
        scene.getStylesheets().add("style.css");
        primaryStage.setScene(scene);
        primaryStage.show();

        currentStep.set(0);
    }
}
