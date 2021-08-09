package thito.nodeflow.internal.ui.launcher.page.settings;

import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.scene.layout.*;
import thito.nodeflow.api.settings.*;
import thito.nodeflow.internal.ui.launcher.page.*;
import thito.nodeflow.library.ui.layout.*;

import java.util.*;

@Deprecated
public class SettingsPeer extends UIComponent {

    @Component("settingsList")
    private final ObjectProperty<VBox> settingsList = new SimpleObjectProperty<>();

    private final SettingsPageImpl page;
    public SettingsPeer(SettingsPageImpl page) {
        this.page = page;
        setLayout(Layout.loadLayout("SettingsUI"));
    }

    @Override
    protected void onLayoutReady() {
        boolean odd = false;
        ObservableBooleanValue changed = new SimpleBooleanProperty();
        ObservableBooleanValue invalid = new SimpleBooleanProperty();
        Map<SettingsItem<?>, SettingsItem<?>> map = new HashMap<>();
//        for (SettingsItem<?> item : NodeFlow.getApplication().getSettings().getSettingsItems()) {
//            SettingsItem<?> clone = item.createTemporary();
//            map.put(item, clone);
//            changed = Bindings.or(changed, item.impl_valueProperty().isNotEqualTo(clone.impl_valueProperty()));
//            SettingsItemPeer peer = new SettingsItemPeer(clone);
//            invalid = Bindings.or(invalid, peer.validProperty());
//            settingsList.get().getChildren().add(peer);
//            if (odd) {
//                peer.getStyleClass().add("settings-item-odd");
//            } else {
//                peer.getStyleClass().add("settings-item-even");
//            }
//            odd = !odd;
//        }
        SettingsFootPeer foot = new SettingsFootPeer();
        foot.getApply().setDisable(true);
        foot.getReset().setDisable(true);
        foot.getApply().disableProperty().bind(Bindings.not(changed).or(invalid));
        foot.getReset().disableProperty().bind(Bindings.not(changed));
        page.changedProperty().bind(changed);
        foot.getApply().setOnAction(event -> {
            for (Map.Entry<SettingsItem<?>, SettingsItem<?>> entry : map.entrySet()) {
                ((SettingsItem<Object>) entry.getKey()).setValue(entry.getValue().getValue());
            }
        });
        foot.getReset().setOnAction(event -> {
            for (Map.Entry<SettingsItem<?>, SettingsItem<?>> entry : map.entrySet()) {
                ((SettingsItem<Object>) entry.getValue()).setValue(entry.getKey().getValue());
            }
        });
        page.getFootButtons().getChildren().setAll(foot);
    }
}
