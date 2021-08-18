package thito.nodeflow.internal;

import javafx.application.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.stage.*;
import thito.nodeflow.library.ui.*;

public class Main extends Application {
    static {
        AdvancedPseudoClass.init();
    }
    @Override
    public void start(Stage stage) throws Exception {
//        ModuleLayer.boot().modules().forEach(m -> {
//            for (String p : m.getPackages()) {
//                m.addExports(p, Main.class.getClassLoader().getUnnamedModule());
//                m.addOpens(p, Main.class.getClassLoader().getUnnamedModule());
//            }
//        });
//        stage.setScene(new SplashScreen().getScene());
//        stage.show();
        NodeFlow.launch();
//        BorderPane pane = new BorderPane();
//        stage.setScene(new Scene(pane));
//        MasonryPane node = new MasonryPane();
//        node.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
//        ScrollPane scroll = new ScrollPane(node);
//        scroll.setFitToWidth(true);
//        scroll.setFitToHeight(true);
//        pane.setCenter(scroll);
//        for (int i = 0; i < 25; i++) {
//            Pane test = new Pane();
//            test.setMinHeight(Math.random() * 100);
//            test.setMinWidth(Math.random() * 70);
//            node.getChildren().add(test);
//            test.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
//        }
//        stage.show();
    }
}
