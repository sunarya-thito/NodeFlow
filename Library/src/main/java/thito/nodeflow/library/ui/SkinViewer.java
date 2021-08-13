package thito.nodeflow.library.ui;

import javafx.animation.*;
import javafx.beans.property.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.util.*;

import java.io.*;

public class SkinViewer {
    private Stage stage;
    private Scene scene;
    private BorderPane root;
    private ScrollPane scroller;
    private BorderPane content;
    private Label problems;
    private StringProperty html = new SimpleStringProperty();
    private ObjectProperty<Skin> skin = new SimpleObjectProperty<>();
    private Timeline ticker;

    private File file;

    public SkinViewer(File file) {
        ticker = new Timeline(new KeyFrame(Duration.millis(100), event -> attemptUpdate()));
        ticker.setCycleCount(-1);
        this.file = file;
        stage = new Stage();
        stage.setTitle(file.getAbsolutePath());
        scene = new Scene(root = new BorderPane());
        SplitPane splitPane = new SplitPane();
        root.setCenter(splitPane);
        splitPane.getItems().add(scroller = new ScrollPane(content = new BorderPane()));
        scroller.setFitToHeight(true);
        scroller.setFitToWidth(true);
        splitPane.getItems().add(new ScrollPane(problems = new Label()));
        stage.setHeight(800);
        stage.setWidth(1200);
        stage.setScene(scene);
        stage.setOnShown(event -> {
            ticker.play();
        });
        stage.setOnHidden(event -> {
            ticker.stop();
        });
        skin.addListener((obs, old, val) -> {
            content.centerProperty().bind(val.rootProperty());
        });
        html.addListener((obs, old, val) -> {
            if (val != null) {
                Skin skin = new Skin();
                SkinParser parser = new SkinParser();
                try {
                    skin.load(val, parser);
                } catch (Throwable t) {
                    StringWriter writer = new StringWriter();
                    t.printStackTrace(new PrintWriter(writer));
                    problems.setText(writer.toString());
                    return;
                }
                problems.setText("");
                this.skin.set(skin);
            }
        });
    }

    public Stage getStage() {
        return stage;
    }

    public void attemptUpdate() {
        StringWriter writer = new StringWriter();
        try (FileReader reader = new FileReader(file)) {
            char[] buffer = new char[1024 * 16];
            int len;
            while ((len = reader.read(buffer, 0, buffer.length)) != -1) {
                writer.write(buffer, 0, len);
            }
        } catch (Throwable t) {
        }
        html.set(writer.toString());
    }

}
