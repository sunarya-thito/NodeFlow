package thito.nodeflow.library.ui.layout;

public interface ComponentAttributeHandler {
    boolean handle(String attribute, String value, Object component) throws LayoutParserException;
}
