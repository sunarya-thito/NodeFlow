package thito.nodeflow.api.ui.dialog.content;

import thito.nodeflow.api.NodeFlow;
import thito.nodeflow.api.locale.I18nItem;
import thito.nodeflow.api.task.Task;
import thito.nodeflow.api.ui.*;

public interface ActionContent extends DialogContent {

    Action[] getActions();

    I18nItem getHeader();

    void setHeader(I18nItem title);

    Pos getHeaderAlignment();

    void setHeaderAlignment(Pos pos);

    interface Action {
        static ActionContent.Action createAction(I18nItem label, Image icon, Task task, boolean closeOnAction) {
            return NodeFlow.getApplication().getUIManager().getDialogManager().createAction(label, icon, task, closeOnAction);
        }

        I18nItem getLabel();

        Task getTask();

        boolean isCloseOnAction();

        Image getIcon();
    }
}
