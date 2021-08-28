package thito.nodeflow.installer.wizard;

import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import thito.nodeflow.installer.*;

import java.io.*;

public class Finish extends Wizard {
    public Finish() {
        titleProperty().set("Finish");
        cancelLabelProperty().set("Done (_D)");
        activeProperty().addListener((obs, old, val) -> {
            if (val) {
                prepare();
            }
        });
    }

    private void prepare() {
        getChildren().clear();

        if (Installing.InstallationError != null) {
            Label label = new Label("Failed to finish the installation");
            StringWriter writer = new StringWriter();
            Installing.InstallationError.printStackTrace(new PrintWriter(writer));
            TextArea textArea = new TextArea(writer.toString());
            textArea.setEditable(false);
            getChildren().addAll(label, textArea);
            backLabelProperty().set("Try Again (_T)");
            Main.EXIT_CODE = 1603;
        } else {
            backLabelProperty().set("Back (_B)");
            Label label = new Label("NodeFlow has been installed successfully into your computer!");
            CheckBox checkBox = new CheckBox("Open NodeFlow");
            checkBox.setSelected(true);
            checkBox.selectedProperty().addListener((obs, old, val) -> Main.RUN_ON_FINISHED = val);
            HBox box = new HBox(checkBox);
            box.setPadding(new Insets(20, 0, 0, 0));
            getChildren().addAll(label, box);
            disableBackProperty().set(true);
            Main.EXIT_CODE = 0;
        }
    }
}
