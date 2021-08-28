package thito.nodeflow.installer.wizard;

import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import thito.nodeflow.installer.*;

import java.io.*;

public class SelectDirectory extends Wizard {
    public SelectDirectory() {
        titleProperty().set("Target Directory");
        Label label = new Label("Select the installation directory:");
        TextField field = new TextField(Main.INSTALLATION_DIR.toString());
        field.textProperty().addListener((obs, old, val) -> {
            if (val == null || val.isEmpty()) {
                field.setText(new File("").getAbsoluteFile().toString());
                return;
            };
            try {
                File file = new File(val).getAbsoluteFile();
                Main.INSTALLATION_DIR = file;
                String path = file.toString();
                if (!val.startsWith(path)) {
                    field.setText(path);
                }
            } catch (Throwable t) {
                field.setText(old);
            }
        });
        Button browse = new Button("Browse");
        browse.setOnAction(event -> {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Target Installation Directory");
            File available = Main.INSTALLATION_DIR;
            while (!available.exists()) available = available.getParentFile();
            chooser.setInitialDirectory(available);
            File result = chooser.showDialog(getScene().getWindow());
            if (result != null) {
                field.setText((Main.INSTALLATION_DIR = result.getAbsoluteFile()).toString());
            }
        });
        HBox box = new HBox(field, browse);
        box.setAlignment(Pos.CENTER);
        box.setSpacing(5);
        HBox.setHgrow(field, Priority.ALWAYS);
        getChildren().addAll(label, box);
    }
}
