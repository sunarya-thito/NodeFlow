package thito.nodeflow.library.ui;

import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.css.*;
import javafx.event.*;
import javafx.scene.*;

public class Pseudos {
    public static final PseudoClass
            HOVERED = PseudoClass.getPseudoClass("hovered");
    public static final PseudoClass ODD = PseudoClass.getPseudoClass("odd");
    public static final PseudoClass SELECTED = PseudoClass.getPseudoClass("selected");
    public static final PseudoClass INVALID = PseudoClass.getPseudoClass("invalid");
    public static final PseudoClass HAS_PARENT = PseudoClass.getPseudoClass("has_parent");
    public static final PseudoClass VISIBLE = PseudoClass.getPseudoClass("visible");
    public static final PseudoClass STAGE_FOCUSED = PseudoClass.getPseudoClass("stage_focused");

    public static void uninstall(Node node, PseudoClass pseudoClass) {
        Object x = node.getProperties().remove(pseudoClass);
        if (x instanceof Object[]) {
            ((ObservableValue<Boolean>) ((Object[]) x)[0]).removeListener(((ChangeListener<? super Boolean>) ((Object[]) x)[1]));
        }
    }

    public static void install(Node node, PseudoClass pseudoClass, ObservableValue<Boolean> value) {
        uninstall(node, pseudoClass);
        node.pseudoClassStateChanged(pseudoClass, value.getValue());
        ChangeListener<Boolean> listener;
        value.addListener(listener = (obs, old, val) -> {
            node.pseudoClassStateChanged(pseudoClass, val);
        });
        node.getProperties().put(pseudoClass, new Object[] {value, listener});
    }

    public static BooleanProperty install(Node node, PseudoClass pseudoClass, EventType<?> trueEvent, EventType<?> falseEvent) {
        BooleanProperty booleanProperty = new SimpleBooleanProperty();
        node.getProperties().put(pseudoClass, booleanProperty);
        if (trueEvent.equals(falseEvent)) { // toggle mode!
            node.addEventHandler(trueEvent, event -> booleanProperty.set(!booleanProperty.get()));
        } else if (falseEvent == null) { // lost focus mode!
            node.addEventHandler(trueEvent, event -> {
                booleanProperty.set(true);
                node.requestFocus();
            });
            node.focusedProperty().addListener((obs, old, val) -> {
                if (!val) {
                    booleanProperty.set(false);
                }
            });
        } else {
            node.addEventHandler(trueEvent, event -> booleanProperty.set(true));
            node.addEventHandler(falseEvent, event -> booleanProperty.set(false));
        }
        install(node, pseudoClass, booleanProperty);
        return booleanProperty;
    }
}
