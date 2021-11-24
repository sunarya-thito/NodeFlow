package thito.nodeflow.ui.form;

import javafx.beans.binding.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.*;
import javafx.css.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import thito.nodeflow.binding.MappedListBinding;
import thito.nodeflow.binding.WeakReferencedListener;
import thito.nodeflow.language.I18n;

import java.lang.reflect.*;

public class FormPane extends VBox {
    private static final PseudoClass INVALID = PseudoClass.getPseudoClass("invalid");
    private Form form;

    private ObservableSet<FormProperty<?>> invalidFormPropertyList = FXCollections.observableSet();
    private ObservableList<FormProperty<?>> formPropertyList = FXCollections.observableArrayList();

    public FormPane(Form form) {
        this.form = form;
        MappedListBinding.bind(getChildren(), formPropertyList, FormField::new);
        for (Field field : form.getClass().getDeclaredFields()) {
            try {
                if (field.getType() == FormProperty.class) {
                    field.setAccessible(true);
                    FormProperty<?> formProperty = (FormProperty<?>) field.get(form);
                    formPropertyList.add(formProperty);
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        validate();
    }

    public void validate() {
        for (Node n : getChildren()) {
            if (n instanceof FormField) {
                ((FormField) n).validateField();
            }
        }
    }

    public ObservableSet<FormProperty<?>> getInvalidFormPropertyList() {
        return invalidFormPropertyList;
    }

    public Form getForm() {
        return form;
    }

    public class FormField extends VBox {
        private FormProperty<?> formProperty;
        private BorderPane content = new BorderPane();
        private ObjectProperty<StringExpression> validation = new SimpleObjectProperty<>();

        public FormField(FormProperty<?> formProperty) {
            this.formProperty = formProperty;

            getStyleClass().add("form-field");
            content.getStyleClass().add("form-field-container");

            getChildren().addAll(new FormName(), content);
            content.setCenter(formProperty.getFormNode().getNode());
        }

        public FormProperty<?> formProperty() {
            return formProperty;
        }
        public void validateField() {
            StringExpression invalid = null;
            for (Validator validator : formProperty.getValidatorList()) {
                I18n inv = validator.validate(formProperty.getValue());
                if (inv != null) {
                    if (invalid == null) {
                        invalid = inv;
                    } else {
                        invalid = invalid.concat("\n").concat(inv);
                    }
                }
            }
            validation.set(invalid);
        }
        public class FormName extends HBox {
            private Label fieldName = new Label();
            private Label invalid = new Label();

            public FormName() {
                getStyleClass().add("form-field-information");
                getChildren().addAll(fieldName, invalid);
                fieldName.getStyleClass().add("form-field-name");
                invalid.getStyleClass().add("form-field-invalid");

                fieldName.textProperty().bind(formProperty.nameProperty());

                formProperty.addListener(new WeakReferencedListener(obs -> validate(), this));
                validation.addListener((obs, old, invalid) -> {
                    if (invalid == null) {
                        this.invalid.textProperty().unbind();
                        this.invalid.textProperty().set(null);
                        this.invalid.pseudoClassStateChanged(INVALID, false);
                        formProperty.validProperty().set(true);
                        invalidFormPropertyList.remove(formProperty);
                    } else {
                        this.invalid.textProperty().bind(invalid);
                        this.invalid.pseudoClassStateChanged(INVALID, true);
                        formProperty.validProperty().set(false);
                        invalidFormPropertyList.add(formProperty);
                    }
                });
            }

        }
    }
}
