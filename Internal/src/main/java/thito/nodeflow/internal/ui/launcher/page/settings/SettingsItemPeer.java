package thito.nodeflow.internal.ui.launcher.page.settings;

import javafx.beans.property.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import thito.nodeflow.api.settings.*;
import thito.nodeflow.library.ui.*;
import thito.nodeflow.library.ui.layout.*;

@Deprecated
public class SettingsItemPeer extends UIComponent {

    @Component("label")
    private final ObjectProperty<Label> label = new SimpleObjectProperty<>();

    @Component("editor")
    private final ObjectProperty<StackPane> editor = new SimpleObjectProperty<>();

    @Component("validator")
    private final ObjectProperty<Label> validator = new SimpleObjectProperty<>();

    private BooleanProperty valid = new SimpleBooleanProperty();

    private final SettingsItem<?> item;
    public SettingsItemPeer(SettingsItem<?> item) {
        this.item = item;
        setLayout(Layout.loadLayout("SettingsItemUI"));
    }

    @Override
    protected void setup(Node node) {
        Pseudos.install(node, Pseudos.INVALID, valid);
        super.setup(node);
    }

    public BooleanProperty validProperty() {
        return valid;
    }

    @Override
    protected void onLayoutReady() {
//        label.get().textProperty().bind(item.getDisplayName().stringBinding());
//        Node peer = item.getEditor().impl_createPeer(item);
//        StackPane.setAlignment(peer, Pos.CENTER_RIGHT);
//        editor.get().getChildren().add(peer);
//        validator.get().managedProperty().bind(validator.get().visibleProperty());
//        item.impl_invalidMessageProperty().addListener(obs -> updateValidity());
        updateValidity();
    }

    private void updateValidity() {
//        I18nItem invalidMessage = item.impl_invalidMessageProperty().get();
//        if (invalidMessage == null) {
//            validator.get().setVisible(false);
//            validator.get().textProperty().unbind();
//            valid.set(false);
//        } else {
//            validator.get().setVisible(true);
//            valid.set(true);
//            validator.get().textProperty().bind(invalidMessage.stringBinding());
//        }
    }
}
