package thito.nodeflow.internal.editor.menu;

import javafx.scene.*;
import javafx.scene.layout.*;
import thito.nodeflow.api.editor.*;
import thito.nodeflow.api.editor.menu.*;
import thito.nodeflow.internal.*;

public class ToolbarImpl implements Toolbar {

    private FileSession session;
    private ToolComponent[] components;

    public ToolbarImpl(FileSession session, ToolComponent... components) {
        this.session = session;
        this.components = components;
    }

    @Override
    public FileSession getEditorSession() {
        return session;
    }

    @Override
    public ToolComponent[] getComponents() {
        return components;
    }

    private HBox box;

    @Override
    public Node impl_getPeer() {
        if (box == null) {
            box = new HBox();
            box.getStyleClass().add("toolbar-box");
            for (ToolComponent component : components) {
                StackPane componentHolder = new StackPane(component.impl_getPeer());
                Toolkit.style(componentHolder, "toolbar-container");
                box.getChildren().add(componentHolder);
            }
        }
        return box;
    }
}
