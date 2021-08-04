package thito.nodeflow.library.ui.layout;

public interface ComponentChildHandler<T> {
    void handleChild(T component, Object child) throws LayoutParserException;
}
