package thito.nodeflow.library.ui.layout.resolver;

import thito.nodeflow.library.ui.layout.*;
import thito.nodeflow.library.ui.layout.handler.child.*;

public class ExternalComponentResolver implements ComponentResolver {
    @Override
    public ComponentFactory createFactory(LayoutParser parser, String type) {
        if (type.startsWith("!")) {
            return new ComponentFactory() {
                @Override
                public Object createComponent() {
                    UIComponent component = new UIComponent() {};
                    component.setLayout(Layout.loadLayout(type.substring(1)));
                    return component;
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
                    return PaneChildHandler.CHILD_HANDLER;
                }
            };
        }
        return null;
    }
}
