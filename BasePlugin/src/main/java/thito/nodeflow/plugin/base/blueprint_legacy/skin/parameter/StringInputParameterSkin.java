package thito.nodeflow.plugin.base.blueprint_legacy.skin.parameter;

import javafx.scene.control.*;
import thito.nodeflow.engine.node.*;
import thito.nodeflow.engine.node.skin.*;
import thito.nodeflow.plugin.base.blueprint_legacy.handler.parameter.*;

public class StringInputParameterSkin extends NodeParameterSkin {

    private Button editButton;
    public StringInputParameterSkin(NodeParameter parameter) {
        super(parameter);
        editButton = new Button();
    }

    public void setValue(String text) {
        editButton.setText(text);
        ((ConstantHolderParameterHandler) getParameter().getHandler()).setValue(text);
    }
}
