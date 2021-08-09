package thito.nodeflow.library.ui.layout.handler.attribute;

import javafx.scene.*;
import thito.nodeflow.api.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.internal.Toolkit;
import thito.nodeflow.library.ui.docker.*;
import thito.nodeflow.library.ui.layout.*;

public class DockerAttributeHandler implements ComponentAttributeHandler {
    @Override
    public boolean handle(String attribute, String value, Object component) throws LayoutParserException {
        if (component instanceof Node) {
            Parent parent = ((Node) component).getParent();
            if (parent instanceof DockerContainer) {
                DockerContainer container = (DockerContainer) parent;
                Toolkit.info("docker label: "+container.getTab().textProperty());
                switch (attribute) {
                    case "docker.label":
                        if (value.startsWith("$(") && value.endsWith("}")) {
                            container.getTab().textProperty().bind(I18n.$(value.substring(2, value.length()-1)).stringBinding());
                        } else {
                            container.getTab().setText(value);
                        }
                        Toolkit.info("docker label: "+container.getTab().textProperty());
                        return true;
                    case "docker.image":
                        if (value.startsWith("!")) {
                            container.getTab().imageProperty().bind(
                                    NodeFlow.getApplication().getResourceManager().getIcon(value.substring(1)).impl_propertyPeer()
                            );
                        } else {
                            container.getTab().imageProperty().bind(
                                    NodeFlow.getApplication().getResourceManager().getImage(value).impl_propertyPeer()
                            );
                        }
                        return true;
                }
            }
        }
        if (component instanceof Docker) {
            switch (attribute) {
                case "docker.type":
                    ((Docker) component).setDockerType(value);
                    return true;
            }
        }
        return false;
    }
}
