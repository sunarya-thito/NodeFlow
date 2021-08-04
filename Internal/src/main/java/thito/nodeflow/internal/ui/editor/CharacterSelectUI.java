package thito.nodeflow.internal.ui.editor;

import javafx.beans.property.*;
import javafx.geometry.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.util.*;
import thito.nodeflow.api.task.*;
import thito.nodeflow.api.ui.dialog.*;
import thito.nodeflow.internal.Toolkit;
import thito.nodeflow.internal.*;
import thito.nodeflow.internal.ui.dialog.content.*;
import thito.nodeflow.library.ui.layout.Component;
import thito.nodeflow.library.ui.layout.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;

public class CharacterSelectUI extends UIComponent {

    @Component("search")
    private ObjectProperty<TextField> search = new SimpleObjectProperty<>();
    @Component("list")
    private ObjectProperty<ListView<Character>> list = new SimpleObjectProperty<>();
    private SimpleTaskQueue taskQueue = new SimpleTaskQueue(TaskThread.BACKGROUND);
    private CharacterSelectContent content;
    private OpenedDialog dialog;
    public CharacterSelectUI(CharacterSelectContent content, OpenedDialog dialog) {
        this.content = content;
        this.dialog = dialog;
        setLayout(Layout.loadLayout("CharacterSelectUI"));
    }

    @Override
    protected void onLayoutReady() {
        font = new JLabel().getFont();
        search.get().textProperty().addListener((obs, old, val) -> updateSearch(val));
        list.get().setCellFactory(new Callback<ListView<Character>, ListCell<Character>>() {
            @Override
            public ListCell<Character> call(ListView<Character> param) {
                ListCell<Character> characterListCell = new ListCell<Character>() {
                    @Override
                    protected void updateItem(Character item, boolean empty) {
                        if (item != null) {
                            Label g = new Label(item.toString());
                            Toolkit.style(g, "character-display");
                            Label c = new Label(Character.getName(item));
                            Toolkit.style(c, "character-name");
                            BorderPane box = new BorderPane();
                            box.setLeft(g);
                            box.setRight(c);
                            BorderPane.setAlignment(g, Pos.CENTER);
                            BorderPane.setAlignment(c, Pos.CENTER);
                            Toolkit.style(box, "character");
                            setGraphic(box);
                        }
                        super.updateItem(item, empty);
                    }
                };
                characterListCell.setOnMousePressed(event -> {
                    if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() >= 2) {
                        content.getResultConsumer().accept(list.get().getSelectionModel().getSelectedItem());
                        dialog.close(null);
                    }
                });
                return characterListCell;
            }
        });
        updateSearch(null);
    }

    private Font font;
    public void updateSearch(String search) {
        taskQueue.putQuery(() -> {
            List<Character> characters = new ArrayList<>();
            for (int i = Character.MIN_CODE_POINT; i < Character.MAX_CODE_POINT; i++) {
                String name = Character.getName(i);
                if (name != null && Character.isBmpCodePoint(i) &&
                        font.canDisplay(i)
                && (search == null || search.isEmpty() || Toolkit.calculateSearchScore(Character.getName(i), search) >= 0)) {
                    characters.add((char) i);
                }
            }
            characters.sort(Comparator.<Character>comparingInt(a -> search == null || search.isEmpty() ? 0 : Toolkit.calculateSearchScore(Character.getName(a), search) * 100 + Toolkit.calculateSearchScore(a.toString(), search)).reversed());
            Task.runOnForeground("sort-characters", () -> {
                this.list.get().getItems().setAll(characters);
                this.list.get().scrollTo(0);
                taskQueue.markReady();
            });
        });
    }

}