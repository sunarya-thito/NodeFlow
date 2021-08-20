package thito.nodeflow.internal;

import javafx.application.*;
import javafx.stage.*;
import thito.nodeflow.library.ui.*;

public class Main extends Application {
    static {
        AdvancedPseudoClass.init();
    }
    @Override
    public void start(Stage stage) throws Exception {
        NodeFlow.launch();
    }
}
