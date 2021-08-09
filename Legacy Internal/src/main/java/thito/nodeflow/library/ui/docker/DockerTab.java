package thito.nodeflow.library.ui.docker;

import javafx.beans.property.*;
import javafx.css.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import thito.nodeflow.internal.*;

public class DockerTab extends AnchorPane {

    private Pane innerBox;
    private ImageView icon = new ImageView();
    private Label label = new Label();
    private Group wrap = new Group(label);
    private Pane indicator = new Pane();

    protected Orientation parentOrientation;

    private BooleanProperty focus = new SimpleBooleanProperty();
    private BooleanProperty selected = new SimpleBooleanProperty();
    private BooleanProperty hover = new SimpleBooleanProperty();
    private BooleanProperty highlightedOpen = new SimpleBooleanProperty();
    private static final PseudoClass indicatorFocusAction = PseudoClass.getPseudoClass("focused");
    private static final PseudoClass backgroundFocus = PseudoClass.getPseudoClass("focused");
    private static final PseudoClass backgroundSelected = PseudoClass.getPseudoClass("selected");

    protected boolean wasOpened;

    public DockerTab(DockerContainer dockerContainer) {
        Toolkit.style(this, "docker-tab");
        Toolkit.style(label, "docker-tab-label");
        Toolkit.style(icon, "docker-tab-icon");

        indicator.setMinHeight(2);
        indicator.setMinWidth(2);

        icon.imageProperty().addListener(x -> updateIconVisibility());

        focus.addListener((obs, old, val) -> {
            indicator.pseudoClassStateChanged(indicatorFocusAction, val);
        });

        selected.addListener((obs, old, val) -> {
            if (val) {
                if (highlightedOpen.get()) getChildren().add(indicator);
            } else {
                getChildren().remove(indicator);
            }
            pseudoClassStateChanged(backgroundSelected, val);
        });

        highlightedOpen.addListener((obs, old, val) -> {
            if (!val) getChildren().remove(indicator);
            else {
                if (selected.get()) {
                    getChildren().add(indicator);
                }
            }
        });

        hover.addListener((obs, old, val) -> {
            pseudoClassStateChanged(backgroundFocus, val);
        });

        if (selected.get()) {
            getChildren().add(indicator);
        }

        addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
            hover.set(true);
        });
        addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
            hover.set(false);
        });
        addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            Docker parent = dockerContainer.getDocker();
            if (parent != null) {
                if (parent.isOpen(dockerContainer)) {
                    parent.hide();
                    wasOpened = false;
                } else {
                    parent.open(dockerContainer);
                    wasOpened = true;
                }
            }
        });
        addEventHandler(MouseEvent.DRAG_DETECTED, event -> {
            Dragboard db = startDragAndDrop(TransferMode.MOVE);
            Docker parent = dockerContainer.getDocker();
            if (parent != null) {
                DockerContainer.DockerSnapshot snapshot = dockerContainer.getSnapshot();
                snapshot.setDocker(parent);
                snapshot.setIndex(parent.flowDocker.getChildren().indexOf(dockerContainer.getTab()));
                parent.updateSnapshot(dockerContainer);
            }
            dockerContainer.updateSnapshot(parent);
            db.setDragView(snapshot(null, null), event.getX(), event.getY());
            Docker.draggingContainer = dockerContainer;
            /*
            Clipboard content can only contain serializable object
            or ByteBuffer. Empty string will act as dummy to make
            the drag view visible.
             */
            ClipboardContent cc = new ClipboardContent();
            cc.put(DockerContext.DOCKER_FORMAT, "");
            db.setContent(cc);
        });

        addEventHandler(DragEvent.DRAG_DONE, event -> {
            if (!event.isAccepted()) {
                DockerContainer.DockerSnapshot snapshot = dockerContainer.getSnapshot();
                snapshot.removeParent();
                Docker parent  = snapshot.getDocker();
                // send back to the last one
                if (parent != null) {
                    int index = snapshot.getIndex();
                    Pane pane = parent.flowDocker;
                    if (index >= 0 && pane.getChildren().size() > 0) {
                        pane.getChildren().remove(dockerContainer.getTab());
//                        System.out.println("put it back on "+index);
                        pane.getChildren().add(Math.min(pane.getChildren().size(), index), dockerContainer.getTab());
                    }
                }
            }
        });
    }

    public BooleanProperty highlightedOpenProperty() {
        return highlightedOpen;
    }

    private void updateIconVisibility() {
        innerBox.getChildren().remove(icon);
        if (icon.imageProperty().get() != null) {
            innerBox.getChildren().add(0, icon);
        }
    }

    private void updateInnerBoxLayout() {
        setTopAnchor(innerBox, 0d);
        setBottomAnchor(innerBox, 0d);
        setLeftAnchor(innerBox, 0d);
        setRightAnchor(innerBox, 0d);
    }

    protected void updateOrientation(Orientation orientation, Pos position) {
        if (innerBox != null) {
            innerBox.getChildren().clear();
        }
        parentOrientation = orientation;
        if (orientation == Orientation.HORIZONTAL) {
            label.setRotate(0);
            innerBox = new HBox(label);
            innerBox.setPadding(new Insets(
                    DockerContext.TOP_PADDING,
                    DockerContext.SIDE_PADDING,
                    DockerContext.TOP_PADDING,
                    DockerContext.SIDE_PADDING
            ));
            ((HBox) innerBox).setAlignment(Pos.CENTER);
            setBottomAnchor(indicator, 0d);
            setTopAnchor(indicator, null);
            setLeftAnchor(indicator, 0d);
            setRightAnchor(indicator, 0d);
        } else {
            if (position.getHpos() == HPos.LEFT) {
                label.setRotate(-90);
                setBottomAnchor(indicator, 0d);
                setTopAnchor(indicator, 0d);
                setLeftAnchor(indicator, null);
                setRightAnchor(indicator, 0d);
            } else {
                label.setRotate(90);
                setBottomAnchor(indicator, 0d);
                setTopAnchor(indicator, 0d);
                setLeftAnchor(indicator, 0d);
                setRightAnchor(indicator, null);
            }
            innerBox = new VBox(wrap);
            innerBox.setPadding(new Insets(
                    DockerContext.SIDE_PADDING,
                    DockerContext.TOP_PADDING,
                    DockerContext.SIDE_PADDING,
                    DockerContext.TOP_PADDING
            ));
            ((VBox) innerBox).setAlignment(Pos.CENTER);
        }
        getChildren().add(innerBox);
        updateIconVisibility();
        updateInnerBoxLayout();
    }

    public BooleanProperty focusProperty() {
        return focus;
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public StringProperty textProperty() {
        return label.textProperty();
    }

    public void setText(String text) {
        label.setText(text);
    }

    public ObjectProperty<Image> imageProperty() {
        return icon.imageProperty();
    }

}
