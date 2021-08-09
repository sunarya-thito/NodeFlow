package thito.nodeflow.internal.node.state;

import thito.nodeflow.api.config.*;
import thito.nodeflow.api.node.state.*;
import thito.nodeflow.internal.node.*;

import java.io.*;

public class GroupStateImpl implements GroupState, Serializable {
    private String name;
    private double x, y, minX, minY, maxX, maxY;
    private StandardNodeModule module;

    public GroupStateImpl(StandardNodeModule module) {
        this.module = module;
    }

    public void setModule(StandardNodeModule module) {
        this.module = module;
    }

    public StandardNodeModule getModule() {
        return module;
    }

    private void writeObject(ObjectOutputStream outputStream) throws IOException {
        StringWriter writer = new StringWriter();
        Section.saveYaml(serialize(), writer);
        outputStream.writeUTF(writer.toString());
    }

    private void readObject(ObjectInputStream inputStream) throws IOException {
        deserialize(Section.loadYaml(new StringReader(inputStream.readUTF())));
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public void setX(double x) {
        this.x = x;
    }

    @Override
    public void setY(double y) {
        this.y = y;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public double getMinX() {
        return minX;
    }

    @Override
    public void setMinX(double minX) {
        this.minX = minX;
    }

    @Override
    public double getMinY() {
        return minY;
    }

    @Override
    public void setMinY(double minY) {
        this.minY = minY;
    }

    @Override
    public double getMaxX() {
        return maxX;
    }

    @Override
    public void setMaxX(double maxX) {
        this.maxX = maxX;
    }

    @Override
    public double getMaxY() {
        return maxY;
    }

    @Override
    public void setMaxY(double maxY) {
        this.maxY = maxY;
    }

    @Override
    public Section serialize() {
        Section section = Section.newMap();
        section.set(x, "x");
        section.set(y, "y");
        section.set(minX, "minX");
        section.set(minY, "minY");
        section.set(maxX, "maxX");
        section.set(maxY, "maxY");
        section.set(name, "name");
        return section;
    }

    @Override
    public void deserialize(Section section) {
        x = section.getDouble("x");
        y = section.getDouble("y");
        minX = section.getDouble("minX");
        minY = section.getDouble("minY");
        maxX = section.getDouble("maxX");
        maxY = section.getDouble("maxY");
        name = section.getString("name");
    }
}
