package thito.nodeflow.library.ui.layout;

public interface ComponentFactory {
    Object createComponent();
    ComponentAttributeHandler getAttributeHandler();
    ComponentChildHandler<?> getChildrenHandler();
}
