package thito.nodeflow.internal.ui.dialog.content.form;

import com.jfoenix.controls.*;
import javafx.beans.property.*;
import javafx.scene.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.ui.dialog.content.*;

public class BooleanFormImpl extends AbstractFormImpl<Boolean> implements FormContent.BooleanForm {

    public BooleanFormImpl(I18nItem question, boolean initialValue, boolean optional) {
        super(question, initialValue, optional);
    }

    private BooleanProperty disable = new SimpleBooleanProperty();
    @Override
    public BooleanProperty impl_disableProperty() {
        return disable;
    }

    @Override
    public void toggle() {
        answer(!getAnswer());
    }

    @Override
    public Node createFieldPeer() {
        JFXCheckBox checkBox = new JFXCheckBox();
        checkBox.disableProperty().bind(disable);
        checkBox.selectedProperty().bindBidirectional(impl_answerProperty());
        return checkBox;
    }
}
