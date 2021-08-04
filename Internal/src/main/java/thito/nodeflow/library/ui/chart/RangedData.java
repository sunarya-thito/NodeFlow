package thito.nodeflow.library.ui.chart;

public class RangedData extends Data {
    private long end;

    public RangedData(DataDisplay name, double value, long start, long end) {
        super(name, value, start);
        this.end = end;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }
}
