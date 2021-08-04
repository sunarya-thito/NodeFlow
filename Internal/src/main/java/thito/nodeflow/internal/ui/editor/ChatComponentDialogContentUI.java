package thito.nodeflow.internal.ui.editor;

import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import net.md_5.bungee.api.chat.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.ui.*;
import thito.nodeflow.api.ui.dialog.Dialog;
import thito.nodeflow.api.ui.dialog.button.*;
import thito.nodeflow.api.ui.dialog.content.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.library.ui.layout.*;
import thito.nodeflow.minecraft.chat.*;

import java.util.*;

public class ChatComponentDialogContentUI extends UIComponent {
    @Component("text")
    private ObjectProperty<ChatComponentArea> text = new SimpleObjectProperty<>();
    private ObjectProperty<BaseComponent[]> property;
    private boolean hoverable, clickable;
    public ChatComponentDialogContentUI(ObjectProperty<BaseComponent[]> property, boolean hoverable, boolean clickable) {
        this.property = property;
        this.hoverable = hoverable;
        this.clickable = clickable;
        setLayout(Layout.loadLayout("MCComponentDialogContentUI"));
    }

    @Override
    protected void onLayoutReady() {
        ChatComponentArea area = text.get();
        if (!hoverable) {
            area.getToolBar().getChildren().remove(area.getHoverBtn());
        }
        if (!clickable) {
            area.getToolBar().getChildren().remove(area.getClickBtn());
        }
        area.setClickEventSupplier((current, event) -> {
            ClickEvent.Action[] a = ClickEvent.Action.values();
            ArrayList<ClickEvent.Action> actions = new ArrayList<>(a.length + 1);
            actions.add(null);
            for (ClickEvent.Action x : a) actions.add(x);
            FormContent.ChoiceForm<ClickEvent.Action> action = FormContent.ChoiceForm.create(I18n.$("action"), current == null ? null : current.getAction(), actions, false);
            FormContent.StringForm execute = FormContent.StringForm.create(I18n.$("execute"), current == null || current.getValue() == null ? "" : current.getValue(), false);
            FormContent content = FormContent.createContent(I18n.$("click-event"), Pos.LEFT, new FormContent.Form[] {action, execute});
            TextDialogButton ok = DialogButton.createTextButton(0, 0, I18n.$("button-ok"), null, click -> {
                click.close();
                if (action.getAnswer() == null) {
                    event.accept(null);
                } else {
                    ClickEvent e = new ClickEvent(action.getAnswer(), execute.getAnswer());
                    event.accept(e);
                }
            });
            Dialog dialog = Dialog.createDialog(content, 0, ok);
            dialog.open(Toolkit.getWindow(this));
        });
        area.setHoverEventSupplier((event, result) -> {
            ObjectProperty<BaseComponent[]> property = new SimpleObjectProperty<>(event);
            ChatComponentDialogContent content = new ChatComponentDialogContent(property);
            TextDialogButton ok = DialogButton.createTextButton(0, 0, I18n.$("button-ok"), null, click -> {
                click.close();
                result.accept(property.get());
            });
            TextDialogButton cancel = DialogButton.createTextButton(1, 0, I18n.$("button-cancel"), null, click -> {
                click.close();
            });
            TextDialogButton remove = DialogButton.createTextButton(1, 0, I18n.$("button-remove-hover"), null, click -> {
                click.close();
                result.accept(null);
            });
            Dialog dialog = Dialog.createDialog(content, 0, remove, cancel, ok);
            dialog.open(Toolkit.getWindow(this));
        });
        area.getHoverBtn().setGraphic(img("hover-event"));
        area.getClickBtn().setGraphic(img("click-event"));
        area.getUndoBtn().setGraphic(img("undo"));
        area.getRedoBtn().setGraphic(img("redo"));
        area.getCutBtn().setGraphic(img("cut"));
        area.getCopyBtn().setGraphic(img("copy"));
        area.getPasteBtn().setGraphic(img("paste"));
        area.getEditor().setComponents(property.get());
        area.getEditor().beingUpdatedProperty().addListener((obs) -> {
            property.set(area.getEditor().getComponents());
        });
        area.wrapTextProperty().bind(text("text-wrap"));
        area.undoTextProperty().bind(text("text-undo"));
        area.redoTextProperty().bind(text("text-redo"));
        area.cutTextProperty().bind(text("text-cut"));
        area.copyTextProperty().bind(text("text-copy"));
        area.pasteTextProperty().bind(text("text-paste"));
        area.boldTextProperty().bind(text("text-bold"));
        area.italicTextProperty().bind(text("text-italic"));
        area.underlineTextProperty().bind(text("text-underline"));
        area.strikeThroughTextProperty().bind(text("text-strikethrough"));
        area.obfuscateTextProperty().bind(text("text-obfuscate"));
        area.flagAsTranslateTextProperty().bind(text("text-flag-as-translate"));
        area.clickTextProperty().bind(text("text-click-action"));
        area.hoverTextProperty().bind(text("text-tooltip"));
    }

    ObservableValue<String> text(String t) {
        return I18n.$(t).stringBinding();
    }

    private ImageView img(String s) {
        ImageView img = new ImageView();
        img.imageProperty().bind(Icon.icon(s).impl_propertyPeer());
        return img;
    }
}
