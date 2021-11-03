package thito.nodeflow.internal.ui;

import javafx.beans.binding.Bindings;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import thito.nodeflow.internal.ui.form.FormPane;

public class FormPanelSkin extends Skin {
    @Component("title")
    Label title;

    @Component("expand")
    ToggleButton expand;

    @Component("content")
    BorderPane content;

    private FormPanel formPanel;

    public FormPanelSkin(FormPanel formPanel) {
        this.formPanel = formPanel;
    }

    @Override
    protected void onLayoutLoaded() {
        title.textProperty().bind(formPanel.titleProperty());
        content.centerProperty().bind(Bindings.when(expand.selectedProperty()).then(formPanel.getPane()).otherwise((FormPane) null));
    }
}
