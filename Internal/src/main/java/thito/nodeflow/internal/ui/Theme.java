package thito.nodeflow.internal.ui;

public class Theme {

    private String name;

    public Theme(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
