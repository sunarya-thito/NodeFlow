package thito.nodeflow.internal.project;

import javafx.beans.property.*;
import javafx.collections.*;
import javafx.scene.Node;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import thito.nodeflow.api.editor.*;
import thito.nodeflow.api.editor.menu.*;
import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.project.*;
import thito.nodeflow.api.project.property.*;
import thito.nodeflow.api.resource.*;
import thito.nodeflow.api.task.*;
import thito.nodeflow.api.ui.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.internal.editor.menu.*;

import java.util.*;

public class ProjectTabImpl extends Tab implements ProjectTab {
    private final ResourceFile file;
    private final BorderPane peer = new BorderPane();
    private EditorWindow window;
    private StringProperty title = new SimpleStringProperty();
    private FileHandler handler;
    private boolean close;
    private ObjectProperty<FileSession> sessionObjectProperty = new SimpleObjectProperty<>();
    private Set<Class<? extends Throwable>> ignoredErrors = new HashSet<>();
    private ObservableList<ComponentProperty<?>> properties = FXCollections.observableArrayList();
    private Group toolbarPeer = new Group();
    private ToolSeparatorImpl separator = new ToolSeparatorImpl();

    public ProjectTabImpl(EditorWindow window, ResourceFile file, FileHandler handler) {
        textProperty().bind(title);
        setContent(peer);
        setClosable(true);
        setOnClosed(event -> {
            window.closeFile(this);
        });
        sessionProperty().addListener((obs, old, val) -> {
            Task.runOnForeground("update-toolbar", () -> {
                toolbarPeer.getChildren().clear();
                if (old != null) {
                    Toolbar toolbar = old.getToolbar();
                    if (toolbar != null) {
                        ((HBox) toolbar.impl_getPeer()).getChildren().remove(separator.impl_getPeer());
                    }
                }
                if (val != null) {
                    Toolbar toolbar = val.getToolbar();
                    if (toolbar != null) {
                        ((HBox) toolbar.impl_getPeer()).getChildren().add(0, separator.impl_getPeer());
                        toolbarPeer.getChildren().add(toolbar.impl_getPeer());
                    }
                }
            });
        });
//        peer.prefHeightProperty().bind(heightProperty());
//        peer.prefWidthProperty().bind(widthProperty());
//        peer.minHeightProperty().bind( peer.getParent()).heightProperty());
//        peer.minWidthProperty().bind(((Region) peer.getParent()).widthProperty());
//        peer.maxHeightProperty().bind(((Region) peer.getParent()).heightProperty());
//        peer.maxWidthProperty().bind(((Region) peer.getParent()).widthProperty());
        this.file = file;
        title.set(file.getName());
        this.window = window;
        this.handler = handler;
        Toolkit.clip(peer);
        reloadFile();
    }

    public Group getToolbarPeer() {
        return toolbarPeer;
    }

    @Override
    public ObservableList<ComponentProperty<?>> getTabProperties() {
        return properties;
    }

    @Override
    public Set<Class<? extends Throwable>> impl_ignoredErrors() {
        return ignoredErrors;
    }

    @Override
    public StringProperty impl_titleProperty() {
        return title;
    }

    @Override
    public void focus() {
        if (getTabPane() != null) {
            getTabPane().getSelectionModel().select(this);
        }
    }

    @Override
    public String getTitle() {
        return file.getName();
    }

    @Override
    public Node impl_getPeer() {
        return peer;
    }

    @Override
    public ResourceFile getFile() {
        return file;
    }

    public ObjectProperty<FileSession> sessionProperty() {
        return sessionObjectProperty;
    }

    @Override
    public void reloadFile() {
        Task.runOnForeground("reload UI", () -> {
            peer.setCenter(new TabOverlayUI());
            handler.createSession(window.getProject(), this, file).andThen(session -> {
                if (close) return;
                sessionObjectProperty.set(session);
                Task.runOnForeground("load-file-content", () -> {
                    peer.setCenter(session.impl_getViewport());
                });
            }).andThenError(error -> {
                Task.runOnForeground("error-info-load", () -> {
                    if (error instanceof MissingProviderException) {
                        peer.setCenter(new ErrorUI(I18n.$("error-open-file"), error, () -> {
                            ignoredErrors.add(MissingProviderException.class);
                            reloadFile();
                        }));
                    } else {
                        peer.setCenter(new ErrorUI(I18n.$("error-open-file"), error, null));
                    }
                });
                error.printStackTrace();
            });
        });
    }

    @Override
    public void closeFile() {
        close = true;
        sessionObjectProperty.set(null);
    }

    @Override
    public boolean isValid() {
        return !(file.reload() instanceof UnknownResource);
    }
}
