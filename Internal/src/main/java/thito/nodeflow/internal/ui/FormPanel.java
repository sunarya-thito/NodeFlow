package thito.nodeflow.internal.ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import thito.nodeflow.internal.ui.form.Form;
import thito.nodeflow.internal.ui.form.FormPane;

public class FormPanel {

    private StringProperty title = new SimpleStringProperty();

    private FormPanelSkin skin;
    private FormPane pane;
    private Form form;

    public FormPanel(Form form) {
        this.form = form;
        skin = new FormPanelSkin(this);
        pane = new FormPane(form);
    }

    public StringProperty titleProperty() {
        return title;
    }

    public FormPane getPane() {
        return pane;
    }

    public Form getForm() {
        return form;
    }

    public Node getNode() {
        return skin;
    }
}
