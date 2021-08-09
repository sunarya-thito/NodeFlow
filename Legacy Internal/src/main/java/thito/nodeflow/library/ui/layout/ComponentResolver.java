package thito.nodeflow.library.ui.layout;

public interface ComponentResolver {
    ComponentFactory createFactory(LayoutParser parser, String type);
}
