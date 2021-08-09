package thito.nodeflow.internal.ui.dialog.content.form;

import com.jfoenix.controls.*;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.scene.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.ui.dialog.content.*;

import java.util.*;

public class StringListFormImpl extends AbstractFormImpl<List<String>> implements FormContent.StringListForm {
    public StringListFormImpl(I18nItem question, ObservableList<String> initialValue, boolean optional) {
        super(question, initialValue, optional);
    }

    private BooleanProperty disable = new SimpleBooleanProperty();
    @Override
    public BooleanProperty impl_disableProperty() {
        return disable;
    }

    @Override
    public List<String> getAnswer() {
        return Collections.unmodifiableList(super.getAnswer());
    }

    @Override
    public boolean answer(List<String> answer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Node createFieldPeer() {
        JFXChipView<String> chipView = new JFXChipView<>();
        chipView.disableProperty().bind(disable);
        Bindings.bindContentBidirectional(chipView.getChips(), (ObservableList<String>) super.getAnswer());
        return chipView;
    }
}
