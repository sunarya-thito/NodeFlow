package thito.nodeflow.library.ui.layout.resolver;

import thito.nodeflow.library.ui.layout.*;
import thito.nodeflow.library.ui.layout.handler.child.*;

public class JavaFXComponentResolver implements ComponentResolver {
    @Override
    public ComponentFactory createFactory(LayoutParser parser, String type) {
        Class<?> clazz = LayoutHelper.classOrNull(type);
        if (clazz != null) {
            ComponentChildHandler<?> componentChildHandler = parser.getChildHandlerMap().get(clazz);
            return new ComponentFactory() {
                @Override
                public Object createComponent() {
                    return LayoutHelper.newInstanceOrNull(clazz);
                }

                @Override
                public ComponentAttributeHandler getAttributeHandler() {
                    return (attribute, value, component) -> {
                        for (ComponentAttributeHandler attributeHandler : parser.getAttributeHandlers()) {
                            if (attributeHandler.handle(attribute, value, component)) {
                                break;
                            }
                        }
                        return true;
                    };
                }

                @Override
                public ComponentChildHandler<?> getChildrenHandler() {
                    return componentChildHandler == null ? PaneChildHandler.CHILD_HANDLER : componentChildHandler;
                }
            };
        }
        return null;
    }
}
