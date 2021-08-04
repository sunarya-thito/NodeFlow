package thito.nodeflow.library.ui.layout.handler.child;

import javafx.scene.*;
import thito.nodeflow.library.ui.docker.*;
import thito.nodeflow.library.ui.layout.*;

public class DockerChildHandler implements ComponentChildHandler<Docker> {
    @Override
    public void handleChild(Docker component, Object child) throws LayoutParserException {
        if (child instanceof Node) {
            DockerContainer container = new DockerContainer();
            container.setViewport((Node) child);
            component.getContainers().add(new DockerContainer());
        }
    }
}
