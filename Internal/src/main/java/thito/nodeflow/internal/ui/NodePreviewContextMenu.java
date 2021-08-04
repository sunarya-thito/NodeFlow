package thito.nodeflow.internal.ui;

import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.stage.*;
import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.node.*;
import thito.nodeflow.api.task.*;

public class NodePreviewContextMenu extends SimpleContextMenu {
    private Stage owner;
    public NodePreviewContextMenu(Stage owner, NodeModule module, NodeProvider provider) {
        super(owner);
        this.owner = owner;
        getStage().initStyle(StageStyle.TRANSPARENT);
        getStage().getScene().setFill(Color.TRANSPARENT);
        getPane().setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
        getPane().setMouseTransparent(true);
        setViewport(new NodePreviewContextMenuPeer(this, module, provider));
    }

    @Override
    protected void attemptUnfocusClose() {
        Task.runOnForeground("check-focus", () -> {
            if (owner.isFocused()) return;
            super.attemptUnfocusClose();
            owner.close();
        });
    }
}
