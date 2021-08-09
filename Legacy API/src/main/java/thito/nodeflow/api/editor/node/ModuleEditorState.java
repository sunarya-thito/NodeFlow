package thito.nodeflow.api.editor.node;

public interface ModuleEditorState {
    double getOffsetX();
    double getOffsetY();
    void setOffsetX(double offsetX);
    void setOffsetY(double offsetY);
    double getPivotX();
    double getPivotY();
    void setPivotX(double pivotX);
    void setPivotY(double pivotY);
    double getZoom();
    void setZoom(double zoom);
    String getNodeLinkStyle();
    void setNodeLinkStyle(String style);
    boolean isSnapToGrid();
    void setSnapToGrid(boolean snapToGrid);
    String getMode();
    void setMode(String mode);
    boolean isPlayAnimation();
    void setPlayAnimation(boolean animation);
}
