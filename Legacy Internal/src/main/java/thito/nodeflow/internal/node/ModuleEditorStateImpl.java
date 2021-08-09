package thito.nodeflow.internal.node;

import thito.nodeflow.api.editor.node.*;

public class ModuleEditorStateImpl implements ModuleEditorState {
    private double offsetX, offsetY, pivotX, pivotY, zoom = 1;
    private String lineStyle = "CURVE", mode = "select";
    private boolean snap, animation;

    @Override
    public double getOffsetX() {
        return offsetX;
    }

    @Override
    public void setOffsetX(double offsetX) {
        this.offsetX = offsetX;
    }

    @Override
    public double getOffsetY() {
        return offsetY;
    }

    @Override
    public void setOffsetY(double offsetY) {
        this.offsetY = offsetY;
    }

    @Override
    public double getPivotX() {
        return pivotX;
    }

    @Override
    public void setPivotX(double pivotX) {
        this.pivotX = pivotX;
    }

    @Override
    public double getPivotY() {
        return pivotY;
    }

    @Override
    public void setPivotY(double pivotY) {
        this.pivotY = pivotY;
    }

    @Override
    public double getZoom() {
        return zoom;
    }

    @Override
    public void setZoom(double zoom) {
        this.zoom = zoom;
    }

    @Override
    public String getNodeLinkStyle() {
        return lineStyle;
    }

    @Override
    public void setNodeLinkStyle(String style) {
        this.lineStyle = style;
    }

    @Override
    public boolean isSnapToGrid() {
        return snap;
    }

    @Override
    public void setSnapToGrid(boolean snapToGrid) {
        this.snap = snapToGrid;
    }

    @Override
    public String getMode() {
        return mode;
    }

    @Override
    public void setMode(String mode) {
        this.mode = mode;
    }

    @Override
    public boolean isPlayAnimation() {
        return animation;
    }

    @Override
    public void setPlayAnimation(boolean animation) {
        this.animation = animation;
    }
}
