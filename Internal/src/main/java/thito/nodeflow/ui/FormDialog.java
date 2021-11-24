package thito.nodeflow.ui;

import javafx.beans.binding.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import thito.nodeflow.language.*;
import thito.nodeflow.ui.dialog.DialogWindow;
import thito.nodeflow.ui.form.*;

import java.util.function.*;

public class FormDialog<T extends Form> {

    public static <T extends Form> FormDialog<T> create(I18n title, T form) {
        return new FormDialog(title, form);
    }

    private I18n title;
    private T form;

    public FormDialog(I18n title, T form) {
        this.title = title;
        this.form = form;
    }

    public T getForm() {
        return form;
    }

    public void show(Consumer<T> result) {
        DialogWindow window = new DialogWindow();
        window.titleProperty().bind(title);
        FormPane formPane = new FormPane(form);
        BorderPane borderPane = new BorderPane(formPane);
        borderPane.getStyleClass().add("form-root");
        Button okButton = new Button();
        okButton.getStyleClass().add("ok-button");
        okButton.setMnemonicParsing(true);
        okButton.disableProperty().bind(Bindings.isNotEmpty(formPane.getInvalidFormPropertyList()));
        okButton.setDefaultButton(true);
        okButton.textProperty().bind(I18n.$("ok-button"));
        Button cancelButton = new Button();
        cancelButton.getStyleClass().add("cancel-button");
        cancelButton.textProperty().bind(I18n.$("cancel-button"));
        cancelButton.setCancelButton(true);
        cancelButton.setMnemonicParsing(true);
        HBox buttons = new HBox(okButton, cancelButton);
        buttons.getStyleClass().add("buttons");
        borderPane.setBottom(buttons);
        window.contentProperty().set(borderPane);
        okButton.setOnAction(event -> {
            window.close();
            result.accept(form);
        });
        cancelButton.setOnAction(event -> {
            window.close();
            result.accept(null);
        });
        formPane.validate();
        window.show();
    }
}
