package thito.nodeflow.library.ui.chart;

public class Data {
    private DataDisplay name;
    private double value;
    private long start;

    public Data(DataDisplay name, double value, long start) {
        this.name = name;
        this.value = value;
        this.start = start;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public DataDisplay getDisplay() {
        return name;
    }

    public void setDisplay(DataDisplay name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
