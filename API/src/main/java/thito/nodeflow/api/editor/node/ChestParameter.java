package thito.nodeflow.api.editor.node;

public interface ChestParameter extends NodeParameter {
    ChestSlot[] getSlots();
    void setRows(int rows);
    int getRows();
    void setTitle(String title);
    String getTitle();
}
