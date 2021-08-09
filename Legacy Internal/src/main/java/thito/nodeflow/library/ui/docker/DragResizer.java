package thito.nodeflow.library.ui.docker;

import javafx.event.*;
import javafx.scene.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;

public class DragResizer {

    /**
     * The margin around the control that a user can click in to start resizing
     * the region.
     */
    private static final int RESIZE_MARGIN = 5;

    private final Region region;

    private double y;

    private boolean initMinHeight;

    private boolean dragging;

    private EventHandler<MouseEvent> mousePress, mouseDrag, mouseMove, mouseRelease;

    private DragResizer(Region aRegion) {
        region = aRegion;
        mousePress = this::mousePressed;
        mouseDrag = this::mouseDragged;
        mouseMove = this::mouseOver;
        mouseRelease = this::mouseReleased;
        enable();
    }

    public void enable() {
        region.addEventHandler(MouseEvent.MOUSE_PRESSED, mousePress);
        region.addEventHandler(MouseEvent.MOUSE_DRAGGED, mouseDrag);
        region.addEventHandler(MouseEvent.MOUSE_MOVED, mouseMove);
        region.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseRelease);
    }

    public void disable() {
        region.removeEventHandler(MouseEvent.MOUSE_PRESSED, mousePress);
        region.removeEventHandler(MouseEvent.MOUSE_DRAGGED, mouseDrag);
        region.removeEventHandler(MouseEvent.MOUSE_MOVED, mouseMove);
        region.removeEventHandler(MouseEvent.MOUSE_RELEASED, mouseRelease);
    }

    public static DragResizer makeResizable(Region region) {
        return new DragResizer(region);
    }

    protected void mouseReleased(MouseEvent event) {
        dragging = false;
        region.setCursor(Cursor.DEFAULT);
    }

    protected void mouseOver(MouseEvent event) {
        if(isInDraggableZone(event) || dragging) {
            region.setCursor(Cursor.S_RESIZE);
        }
        else {
            region.setCursor(Cursor.DEFAULT);
        }
    }

    protected boolean isInDraggableZone(MouseEvent event) {
        return event.getY() > (region.getHeight() - RESIZE_MARGIN);
    }

    protected void mouseDragged(MouseEvent event) {
        if(!dragging) {
            return;
        }

        double mousey = event.getY();

        double newHeight = region.getMinHeight() + (mousey - y);

        region.setMinHeight(newHeight);

        y = mousey;
    }

    protected void mousePressed(MouseEvent event) {

        // ignore clicks outside of the draggable margin
        if(!isInDraggableZone(event)) {
            return;
        }

        dragging = true;

        // make sure that the minimum height is set to the current height once,
        // setting a min height that is smaller than the current height will
        // have no effect
        if (!initMinHeight) {
            region.setMinHeight(region.getHeight());
            initMinHeight = true;
        }

        y = event.getY();
    }
}
