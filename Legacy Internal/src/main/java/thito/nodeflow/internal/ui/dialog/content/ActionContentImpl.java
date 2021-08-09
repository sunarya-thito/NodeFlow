package thito.nodeflow.internal.ui.dialog.content;

import javafx.beans.property.*;
import javafx.event.*;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.task.*;
import thito.nodeflow.api.ui.Image;
import thito.nodeflow.api.ui.*;
import thito.nodeflow.api.ui.dialog.*;
import thito.nodeflow.api.ui.dialog.content.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.library.ui.layout.*;

public class ActionContentImpl implements ActionContent {
    private I18nItem header;
    private Pos alignment;
    private Action[] actions;

    public ActionContentImpl(I18nItem header, Pos alignment, Action... actions) {
        this.header = header;
        this.alignment = alignment;
        this.actions = actions;
    }

    @Override
    public I18nItem getHeader() {
        return header;
    }

    @Override
    public void setHeader(I18nItem title) {
        header = title;
    }

    @Override
    public Pos getHeaderAlignment() {
        return alignment;
    }

    @Override
    public void setHeaderAlignment(Pos pos) {
        alignment = pos;
    }

    @Override
    public Pane impl_createPeer(OpenedDialog dialog) {
        return new ActionContentDialog(dialog);
    }

    @Override
    public Action[] getActions() {
        return actions;
    }

    public static class ActionImpl implements Action {
        private I18nItem label;
        private thito.nodeflow.api.ui.Image icon;
        private Task task;
        private boolean closeOnAction;

        public ActionImpl(I18nItem label, thito.nodeflow.api.ui.Image icon, Task task, boolean closeOnAction) {
            this.label = label;
            this.icon = icon;
            this.task = task;
            this.closeOnAction = closeOnAction;
        }

        @Override
        public I18nItem getLabel() {
            return label;
        }

        @Override
        public Image getIcon() {
            return icon;
        }

        @Override
        public Task getTask() {
            return task;
        }

        @Override
        public boolean isCloseOnAction() {
            return closeOnAction;
        }
    }

    public class ActionContentDialog extends UIComponent {
        @Component("title")
        private final ObjectProperty<Label> title = new SimpleObjectProperty<>();

        @Component("buttons")
        private final ObjectProperty<HBox> buttons = new SimpleObjectProperty<>();

        private OpenedDialog dialog;

        public ActionContentDialog(OpenedDialog dialog) {
            this.dialog = dialog;
            setLayout(Layout.loadLayout("ActionContentDialogUI"));
        }

        @Override
        protected void onLayoutReady() {
            title.get().textProperty().bind(header.stringBinding());
            title.get().setAlignment(Toolkit.posToPos(getHeaderAlignment()));
            for (Action action : getActions()) {
                buttons.get().getChildren().add(new ActionDialog(action, dialog));
            }
        }
    }

    public class ActionDialog extends UIComponent {

        private final ColorAdjust adjust = new ColorAdjust(0, -1, 0, 0);

        @Component("icon")
        private final ObjectProperty<ImageView> icon = new SimpleObjectProperty<>();

        @Component("label")
        private final ObjectProperty<Label> label = new SimpleObjectProperty<>();

        private final Action action;
        private OpenedDialog dialog;
        public ActionDialog(Action action, OpenedDialog dialog) {
            this.dialog = dialog;
            this.action = action;
            setLayout(Layout.loadLayout("ActionDialogUI"));
            setEffect(adjust);
            setOnMouseEntered(event -> {
                setEffect(null);
            });
            setOnMouseExited(event -> {
                setEffect(adjust);
            });
        }

        @Override
        protected void onLayoutReady() {
            label.get().textProperty().bind(action.getLabel().stringBinding());
            if (action.getIcon() != null) {
                icon.get().imageProperty().bind(action.getIcon().impl_propertyPeer());
            }
            setOnMousePressed(Event::consume);
            setOnMouseDragged(Event::consume);
            setOnMouseClicked(event -> {
                event.consume();
                if (action.isCloseOnAction()) {
                    dialog.close(null);
                }
                if (action.getTask() != null && action.getTask().getThread() != null) {
                    action.getTask().getThread().runTask(action.getTask());
                }
            });
        }
    }
}
