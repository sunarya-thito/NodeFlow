package thito.nodeflow.internal.ui.dashboard;

import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import thito.nodeflow.library.ui.*;
import thito.nodeflow.library.ui.Skin;

import java.util.*;

public class ProjectHelper {
    public static Color randomPastelColor() {
        Random random = new Random();
        return Color.rgb(100 + random.nextInt(155), 100 + random.nextInt(155), 100 + random.nextInt(155));
    }

    public static Image create(String name, double width, double height) {
        BorderPane pane = new BorderPane();
        pane.setPrefSize(width, height);
        pane.setMaxSize(width, height);
        Label label = new Label(collectPrefix(name));
        label.getStylesheets().add("rsrc:Themes/"+ThemeManager.getInstance().getTheme().getName()+"/StyleSheets/"+ Skin.class.getName().replace('.', '/') +".css");
        label.setTextFill(Color.WHITE);
        label.setStyle("-fx-font-family: 'AXIS Extra Bold'; -fx-font-size: 60;");
        pane.setCenter(label);
        pane.setBackground(new Background(new BackgroundFill(randomPastelColor(), new CornerRadii(5), null)));
        Scene scene = new Scene(pane);
        scene.setFill(Color.TRANSPARENT);
        return pane.snapshot(null, new WritableImage((int) width, (int)height));
    }

    public static String collectPrefix(String name) {
        StringBuilder builder = new StringBuilder();
        for (String s : name.split("\\s+")) {
            if (!s.isEmpty()) {
                char c = s.charAt(0);
                builder.append(Character.toUpperCase(c));
            }
        }
        return builder.toString();
    }
}
