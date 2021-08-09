package thito.nodeflow.api.node.state;

public interface GroupState extends State {
    String getName();
    double getX();
    double getY();
    double getMinX();
    double getMinY();
    double getMaxX();
    double getMaxY();

    void setX(double x);
    void setY(double y);

    void setName(String name);
    void setMinX(double minX);
    void setMinY(double minY);
    void setMaxX(double maxX);
    void setMaxY(double maxY);
}
