package thito.nodeflow.javascript;

public class SourceCode implements AutoCloseable {

    private StringBuilder builder = new StringBuilder();
    public SourceCode append(Object any) {
        builder.append(any);
        return this;
    }

    private SourceCode previous;
    private SourceCode next;

    public SourceCode(SourceCode previous) {
        this.previous = previous;
    }

    public SourceCode open() {
        if (next != null) throw new IllegalStateException("already opened");
        next = new SourceCode(this);
        return next;
    }

    public void close() {
        if (previous == null || previous.next != this) throw new IllegalStateException("already closed");
        previous.next = null;
        previous.builder.append(builder);
    }

}
