package thito.nodeflow.installer.wizard;

import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import thito.nodeflow.installer.*;

public class SelectBranch extends Wizard {
    public SelectBranch() {
        titleProperty().set("Select Branch");
        Label branch = new Label("Select which branch do you want to install on your computer");
        RadioButton master = new RadioButton("Master");
        RadioButton deploy = new RadioButton("Snapshot");
        master.selectedProperty().addListener((obs, old, val) -> {
            System.setProperty("nodeflow.branch", "master");
        });
        deploy.selectedProperty().addListener((obs, old, val) -> {
            System.setProperty("nodeflow.branch", "deploy");
        });
        master.setSelected(true);
        ToggleGroup group = new ToggleGroup();
        master.setToggleGroup(group);
        deploy.setToggleGroup(group);
        Label masterDescription = new Label("Master branch is the main branch of NodeFlow. It is recommended to use this branch for stability.");
        Label deployDescription = new Label("Snapshot branch is a beta branch. Use this to get snapshot features. This branch might be unstable!.");
        masterDescription.setWrapText(true);
        deployDescription.setWrapText(true);
        VBox masterContainer = new VBox(master, masterDescription);
        VBox deployContainer = new VBox(deploy, deployDescription);
        masterContainer.setSpacing(5);
        deployContainer.setSpacing(5);
        VBox container = new VBox(masterContainer, deployContainer);
        container.setPadding(new Insets(10));
        container.setSpacing(20);
        getChildren().addAll(branch, container);
    }
}
