package thito.nodeflow.internal.ui.form;

import javafx.beans.property.*;
import thito.nodeflow.internal.language.*;

import java.util.*;

public class FormProperty<T> extends SimpleObjectProperty<T> {
    private BooleanProperty valid = new SimpleBooleanProperty();
    private LinkedList<Validator<T>> validatorLinkedList = new LinkedList<>();
    private I18n name;
    private FormNode<T> formNode;

    public FormProperty(I18n name, FormNode<T> node) {
        this.name = name;
        formNode = node;
        formNode.initialize(this);
    }

    public FormProperty(I18n name, T t, FormNode<T> node) {
        super(t);
        this.name = name;
        formNode = node;
        formNode.initialize(this);
    }

    public LinkedList<Validator<T>> getValidatorList() {
        return validatorLinkedList;
    }

    public I18n nameProperty() {
        return name;
    }

    public FormNode<T> getFormNode() {
        return formNode;
    }

    public FormProperty<T> validate(Validator<T> validator) {
        validatorLinkedList.add(validator);
        return this;
    }

    public BooleanProperty validProperty() {
        return valid;
    }
}
