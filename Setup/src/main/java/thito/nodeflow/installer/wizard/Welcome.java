package thito.nodeflow.installer.wizard;

import javafx.scene.control.*;
import thito.nodeflow.installer.*;

public class Welcome extends Wizard {
    public Welcome() {
        titleProperty().set("Welcome");
        Label label = new Label("You are going to install NodeFlow into your computer. " +
                "You will be guided through this installation wizard. \n\n" +
                "This requires an internet connection, so make sure you have a (stable) internet connection.\n\n" +
                "Press the Next button to continue.");
        label.setWrapText(true);
        getChildren().add(label);
    }
}
