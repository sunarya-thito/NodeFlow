package thito.nodeflow.internal;

import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;

import javax.swing.*;
import java.io.*;
import java.util.*;

public class EULA {

    static boolean ACCEPT = false;

    public static int isEulaAccepted() {
        File file = new File("eula.txt");
        if (file.exists()) {
            Properties properties = new Properties();
            try (Reader reader = new FileReader(file)) {
                properties.load(reader);
                return Boolean.parseBoolean(properties.getProperty("accept-eula", "false")) ? 1 : 0;
            } catch (Throwable t) {
            }
        }
        return -1;
    }

    public static void saveEula() {
        File file = new File("eula.txt");
        try (Writer writer = new FileWriter(file)) {
            Properties properties = new Properties();
            properties.setProperty("accept-eula", String.valueOf(ACCEPT));
            properties.store(writer, "NodeFlow EULA can be seen if this value is false or first time app launch");
        } catch (Throwable t) {
            t.printStackTrace();
            JOptionPane.showMessageDialog(null, "Please check the latest log", "Application Crash", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private Stage stage = new Stage();
    private BorderPane root = new BorderPane();
    private Scene scene = new Scene(root);
    public EULA() {
        stage.setWidth(450);
        stage.setHeight(600);
        stage.setScene(scene);
        stage.initStyle(StageStyle.UTILITY);
        stage.setTitle("EULA");

        TextArea area = new TextArea();
        area.setWrapText(true);
        area.setEditable(false);
        try {
            byte[] read = Toolkit.readAll(EULA.class.getClassLoader().getResourceAsStream("LICENSE"));
            area.setText(new String(read));
        } catch (Throwable t) {
            throw new Error(t);
        }

        Button accept = new Button("Accept");
        accept.setOnAction(event -> {
            ACCEPT = true;
            stage.close();
        });
        Button decline = new Button("Decline");
        decline.setOnAction(event -> {
            System.exit(0);
        });
        FlowPane buttons = new FlowPane(decline, accept);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.setPadding(new Insets(10, 15, 10, 15));
        buttons.setHgap(10);
        root.setBottom(buttons);

        root.setCenter(area);
    }

    public void show() {
        stage.showAndWait();
    }

    public void hide() {
        stage.hide();
    }
}
