package thito.nodeflow.plugin.base.blueprint.skin.parameter;

import javafx.scene.control.*;
import org.apache.commons.lang3.reflect.*;
import thito.nodeflow.engine.node.*;
import thito.nodeflow.engine.node.skin.*;
import thito.nodeflow.internal.binding.*;
import thito.nodeflow.plugin.base.blueprint.*;

public class TypeInfoParameterSkin extends NodeParameterSkin {
    private GenericStorage.GenericBinding genericBinding;

    public TypeInfoParameterSkin(NodeParameter parameter, GenericStorage.GenericBinding genericBinding) {
        super(parameter);
        this.genericBinding = genericBinding;
        Label label = new Label();
        label.getStyleClass().add("type-info-parameter-skin");
        label.textProperty().bind(MappedBinding.map(genericBinding, TypeUtils::toString));
        this.editorPane.setCenter(label);
    }
}
