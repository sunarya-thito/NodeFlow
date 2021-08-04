package thito.nodeflow.internal.ui.dialog.content.form;

import javafx.beans.property.*;
import javafx.scene.*;
import javafx.scene.control.*;
import thito.nodeflow.api.locale.*;

public class IntegerNumberFormImpl extends AbstractFormImpl<Integer> {
    private boolean allowDecimals; // if false, that means it only accepts integer and/or long

    public IntegerNumberFormImpl(I18nItem question, int initialValue, boolean optional) {
        super(question, initialValue, optional);
    }

    private BooleanProperty disable = new SimpleBooleanProperty();
    @Override
    public BooleanProperty impl_disableProperty() {
        return disable;
    }

    @Override
    public Node createFieldPeer() {
        Spinner<Integer> spinner = new Spinner<>(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 255));
        spinner.disableProperty().bind(disable);
        spinner.getValueFactory().valueProperty().bindBidirectional(impl_answerProperty());
        return spinner;
    }
}
