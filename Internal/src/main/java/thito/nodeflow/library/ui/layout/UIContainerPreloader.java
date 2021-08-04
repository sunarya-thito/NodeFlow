package thito.nodeflow.library.ui.layout;

import javafx.beans.property.*;
import javafx.scene.layout.*;
import thito.nodeflow.library.ui.*;

public class UIContainerPreloader extends UIComponent {

    @Component("spinner")
    private final ObjectProperty<BetterSpinner> spinner = new SimpleObjectProperty<>();

    @Component("viewport")
    private final ObjectProperty<VBox> viewport = new SimpleObjectProperty<>();

    public UIContainerPreloader() {
        setLayout(Layout.loadLayout("ContentPreloaderUI"));
    }

    public VBox getViewport() {
        return viewport.get();
    }

    public BetterSpinner getSpinner() {
        return spinner.get();
    }
}
