package thito.nodeflow.internal.ui;

import javafx.event.*;

public enum FXEvent {
    ANY(javafx.event.Event.ANY),
    MOUSEEVENT_ANY(javafx.scene.input.MouseEvent.ANY),
    MOUSE_PRESSED(javafx.scene.input.MouseEvent.MOUSE_PRESSED),
    MOUSE_RELEASED(javafx.scene.input.MouseEvent.MOUSE_RELEASED),
    MOUSE_CLICKED(javafx.scene.input.MouseEvent.MOUSE_CLICKED),
    MOUSE_ENTERED_TARGET(javafx.scene.input.MouseEvent.MOUSE_ENTERED_TARGET),
    MOUSE_ENTERED(javafx.scene.input.MouseEvent.MOUSE_ENTERED),
    MOUSE_EXITED_TARGET(javafx.scene.input.MouseEvent.MOUSE_EXITED_TARGET),
    MOUSE_EXITED(javafx.scene.input.MouseEvent.MOUSE_EXITED),
    MOUSE_MOVED(javafx.scene.input.MouseEvent.MOUSE_MOVED),
    MOUSE_DRAGGED(javafx.scene.input.MouseEvent.MOUSE_DRAGGED),
    DRAG_DETECTED(javafx.scene.input.MouseEvent.DRAG_DETECTED),
    ACTION(javafx.event.ActionEvent.ACTION),
    ACTIONEVENT_ANY(javafx.event.ActionEvent.ANY),
    CHECKBOXTREEITEM_TREEMODIFICATIONEVENT_ANY(javafx.scene.control.CheckBoxTreeItem.TreeModificationEvent.ANY),
    DIALOGEVENT_ANY(javafx.scene.control.DialogEvent.ANY),
    DIALOG_SHOWING(javafx.scene.control.DialogEvent.DIALOG_SHOWING),
    DIALOG_SHOWN(javafx.scene.control.DialogEvent.DIALOG_SHOWN),
    DIALOG_HIDING(javafx.scene.control.DialogEvent.DIALOG_HIDING),
    DIALOG_HIDDEN(javafx.scene.control.DialogEvent.DIALOG_HIDDEN),
    DIALOG_CLOSE_REQUEST(javafx.scene.control.DialogEvent.DIALOG_CLOSE_REQUEST),
    INPUTEVENT_ANY(javafx.scene.input.InputEvent.ANY),
    LISTVIEW_EDITEVENT_ANY(javafx.scene.control.ListView.EditEvent.ANY),
    MEDIA_ERROR(javafx.scene.media.MediaErrorEvent.MEDIA_ERROR),
    SCROLLTOEVENT_ANY(javafx.scene.control.ScrollToEvent.ANY),
    SORTEVENT_ANY(javafx.scene.control.SortEvent.ANY),
    TABLECOLUMN_CELLEDITEVENT_ANY(javafx.scene.control.TableColumn.CellEditEvent.ANY),
    TRANSFORM_CHANGED(javafx.scene.transform.TransformChangedEvent.TRANSFORM_CHANGED),
    TRANSFORMCHANGEDEVENT_ANY(javafx.scene.transform.TransformChangedEvent.ANY),
    TREEITEM_TREEMODIFICATIONEVENT_ANY(javafx.scene.control.TreeItem.TreeModificationEvent.ANY),
    TREETABLECOLUMN_CELLEDITEVENT_ANY(javafx.scene.control.TreeTableColumn.CellEditEvent.ANY),
    TREETABLEVIEW_EDITEVENT_ANY(javafx.scene.control.TreeTableView.EditEvent.ANY),
    TREEVIEW_EDITEVENT_ANY(javafx.scene.control.TreeView.EditEvent.ANY),
    WEBERROREVENT_ANY(javafx.scene.web.WebErrorEvent.ANY),
    USER_DATA_DIRECTORY_ALREADY_IN_USE(javafx.scene.web.WebErrorEvent.USER_DATA_DIRECTORY_ALREADY_IN_USE),
    USER_DATA_DIRECTORY_IO_ERROR(javafx.scene.web.WebErrorEvent.USER_DATA_DIRECTORY_IO_ERROR),
    USER_DATA_DIRECTORY_SECURITY_ERROR(javafx.scene.web.WebErrorEvent.USER_DATA_DIRECTORY_SECURITY_ERROR),
    WEBEVENT_ANY(javafx.scene.web.WebEvent.ANY),
    RESIZED(javafx.scene.web.WebEvent.RESIZED),
    STATUS_CHANGED(javafx.scene.web.WebEvent.STATUS_CHANGED),
    VISIBILITY_CHANGED(javafx.scene.web.WebEvent.VISIBILITY_CHANGED),
    ALERT(javafx.scene.web.WebEvent.ALERT),
    WINDOWEVENT_ANY(javafx.stage.WindowEvent.ANY),
    WINDOW_SHOWING(javafx.stage.WindowEvent.WINDOW_SHOWING),
    WINDOW_SHOWN(javafx.stage.WindowEvent.WINDOW_SHOWN),
    WINDOW_HIDING(javafx.stage.WindowEvent.WINDOW_HIDING),
    WINDOW_HIDDEN(javafx.stage.WindowEvent.WINDOW_HIDDEN),
    WINDOW_CLOSE_REQUEST(javafx.stage.WindowEvent.WINDOW_CLOSE_REQUEST),
    WORKERSTATEEVENT_ANY(javafx.concurrent.WorkerStateEvent.ANY),
    WORKER_STATE_READY(javafx.concurrent.WorkerStateEvent.WORKER_STATE_READY),
    WORKER_STATE_SCHEDULED(javafx.concurrent.WorkerStateEvent.WORKER_STATE_SCHEDULED),
    WORKER_STATE_RUNNING(javafx.concurrent.WorkerStateEvent.WORKER_STATE_RUNNING),
    WORKER_STATE_SUCCEEDED(javafx.concurrent.WorkerStateEvent.WORKER_STATE_SUCCEEDED),
    WORKER_STATE_CANCELLED(javafx.concurrent.WorkerStateEvent.WORKER_STATE_CANCELLED),
    WORKER_STATE_FAILED(javafx.concurrent.WorkerStateEvent.WORKER_STATE_FAILED),
    ;
    private EventType<?> type;
    FXEvent(EventType<?> type) {
        this.type = type;
    }

    public EventType<?> getType() {
        return type;
    }
}