package thito.nodeflow.internal.ui.dialog.content.form;

import javafx.beans.property.*;
import javafx.scene.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.ui.dialog.content.*;

public abstract class AbstractFormImpl<T> implements FormContent.Form<T> {
    private I18nItem question;
    private ObjectProperty<T> answer = new SimpleObjectProperty<>();
    private boolean optional;

    public AbstractFormImpl(I18nItem question, T initialValue, boolean optional) {
        answer.set(initialValue);
        this.question = question;
        this.optional = optional;
    }

    @Override
    public boolean optional() {
        return optional;
    }

    @Override
    public boolean answer(T answer) {
        this.answer.set(answer);
        return true;
    }

    @Override
    public I18nItem getQuestion() {
        return question;
    }

    @Override
    public T getAnswer() {
        return answer.get();
    }

    @Override
    public ObjectProperty<T> impl_answerProperty() {
        return answer;
    }

    public abstract Node createFieldPeer();
}
