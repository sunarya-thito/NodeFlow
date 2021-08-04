package thito.nodeflow.internal.node.property;

import javafx.beans.property.*;
import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.node.eventbus.*;
import thito.nodeflow.api.node.eventbus.command.*;
import thito.nodeflow.internal.node.*;
import thito.nodeflow.internal.node.property.type.*;
import thito.nodeflow.internal.node.state.*;

import java.util.*;

public class ModuleMemberProperties {
    public static List<ComponentPropertyImpl<?>> createProperties(ModuleMember member) {
        List<ComponentPropertyImpl<?>> property = new ArrayList<>();
        if (member instanceof Node) {
            StringProperty stringProperty2 = new SimpleStringProperty(((ComponentStateImpl) member.getState()).getID().toString());
            ComponentPropertyImpl<String> uuid = new ComponentPropertyImpl<>(I18n.$("property-uuid"), stringProperty2, StringPropertyType.TYPE);
            uuid.disableProperty().set(true);
            property.add(uuid);

            StringProperty stringProperty = new SimpleStringProperty(((ComponentStateImpl) member.getState()).getName()) {
                @Override
                protected void invalidated() {
                    ((ComponentStateImpl) member.getState()).setName(get());
                    ((StandardNodeModule) ((Node) member).getModule()).attemptSave();
                }
            };
            ComponentPropertyImpl<String> name = new ComponentPropertyImpl<>(I18n.$("property-name"), stringProperty, StringPropertyType.TYPE);
            property.add(name);

            ObjectProperty<NodeTag> tagProp = ((ComponentStateImpl) member.getState()).getTag();
            ComponentPropertyImpl<NodeTag> tag = new ComponentPropertyImpl<>(I18n.$("property-tag"), tagProp, new EnumPropertyType<>(NodeTag.class));
            property.add(tag);

            if (((ComponentStateImpl) member.getState()).getProvider() instanceof EventProvider) {
                ObjectProperty<EventPriority> prop = new SimpleObjectProperty<EventPriority>(fromStringOrDefault(((ComponentStateImpl) member.getState()).getExtras().getString("eventPriority"))) {
                    @Override
                    protected void invalidated() {
                        ((ComponentStateImpl) member.getState()).getExtras().set(get().name(), "eventPriority");
                        ((StandardNodeModule) ((Node) member).getModule()).attemptSave();
                    }
                };
                ComponentPropertyImpl<EventPriority> priority = new ComponentPropertyImpl<>(I18n.$("property-priority"), prop, new EnumPropertyType<>(EventPriority.class));
                property.add(priority);
            }

            if (member instanceof CommandNode) {
                String[] properties = { "command-description", "command-permission", "command-permission-message", "command-invalid-argument-message", "command-invalid-sender-message"};
                for (String prop : properties) {
                    StringProperty string = new SimpleStringProperty(((ComponentStateImpl) member.getState()).getExtras().getString(prop)) {
                        @Override
                        protected void invalidated() {
                            ((ComponentStateImpl) member.getState()).getExtras().set(get(), prop);
                            ((StandardNodeModule) ((Node) member).getModule()).attemptSave();
                        }
                    };
                    ComponentPropertyImpl<String> componentProperty = new ComponentPropertyImpl<>(I18n.$(prop), string, new StringPropertyType());
                    property.add(componentProperty);
                }
            }

        } else if (member instanceof NodeGroup) {
            StringProperty strProp = ((NodeGroupImpl) member).impl_getPeer().getGroupName();
            ComponentPropertyImpl<String> name = new ComponentPropertyImpl<>(I18n.$("property-name"), strProp, StringPropertyType.TYPE);
            property.add(name);
        }
        return property;
    }

    static EventPriority fromStringOrDefault(String value) {
        try {
            return EventPriority.valueOf(value);
        } catch (Throwable t) {
            return EventPriority.NORMAL;
        }
    }
}
