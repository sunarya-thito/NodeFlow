package thito.nodeflow.internal.ui;

import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.*;
import thito.nodeflow.api.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.internal.Toolkit;
import thito.nodeflow.internal.*;
import thito.nodeflow.library.ui.decoration.popup.*;

public class AboutDialog extends Stage implements ExceptionalStage {
    public AboutDialog() {
        requestFocus();
        focusedProperty().addListener((obs, old, val) -> {
            if (!val) {
                close();
            }
        });
        setResizable(false);
        initOwner(PopupBase.getInvisibleParent());
        initStyle(StageStyle.TRANSPARENT);
        initializeInterface();
        show();
    }

    private double offX, offY;

    private void initializeInterface() {
        LegacySplashUI ui = new LegacySplashUI();
        ui.getRoot().setMaxHeight(100);
        Text text = new Text("NODEFLOW");
        text.setFill(Color.WHITE);
        text.setEffect(new DropShadow(10, Color.color(0, 0, 0, 0.25)));
        text.setFont(Font.font("AXIS Extra Bold", 30));
        Text versionText = new Text(NodeFlow.getApplication().getVersion().toString());
        versionText.setFont(Font.font("Roboto Condensed", 14));
        versionText.effectProperty().bind(text.effectProperty());
        versionText.fillProperty().bind(text.fillProperty());
        FlowPane flow = new FlowPane(text, versionText);
        flow.setOrientation(Orientation.VERTICAL);
        flow.setPadding(new Insets(0, 0, 0, 20));
        flow.setAlignment(Pos.CENTER_LEFT);
        ui.getRoot().getChildren().add(flow);
        BorderPane pane = new BorderPane();
        pane.setTop(ui.getRoot());
        VBox box = new VBox();
        box.setPadding(new Insets(10, 20, 10, 20));
        Label aboutText = new Label(
                "NodeFlow\n" +
                        "Created by Thito Yalasatria Sunarya\n\n" +
                        "Developers:\n" +
                        "Thito Yalsatria Sunarya\n" +
                        "\n" +
                        "Wiki Team:\n" +
                        "John Lloyd Luciano\n\n"+I18n.$("translator").getRawString()+":\n"+I18n.$("translators").getString()+" - "+I18n.$("name")
        );
        aboutText.setFont(Font.font("Roboto", 12));
        aboutText.setTextFill(Color.BLACK);
        box.getChildren().add(aboutText);
        pane.setCenter(box);
        Toolkit.clip(pane);
        Rectangle clip = (Rectangle) pane.getClip();
        clip.setArcHeight(15);
        clip.setArcWidth(15);
        pane.setBackground(new Background(new BackgroundFill(Color.color(0.97, 0.97, 0.97), null, null)));
        Toolkit.style(pane, "about-dialog");
        Toolkit.style(aboutText, "about-dialog-text");
        StackPane shadowing = new StackPane(pane);
        shadowing.setEffect(new DropShadow(30, Color.color(0, 0, 0, 0.4)));
        shadowing.setPadding(new Insets(30));
        shadowing.setBackground(Background.EMPTY);
        Scene scene = new Scene(shadowing, 500, 400);
        scene.setOnMousePressed(event -> {
            offX = event.getScreenX() - getX();
            offY = event.getScreenY() - getY();
        });
        scene.setOnMouseDragged(event -> {
            setX(event.getScreenX() - offX);
            setY(event.getScreenY() - offY);
        });
        scene.setFill(Color.TRANSPARENT);
        setScene(scene);
    }
}
