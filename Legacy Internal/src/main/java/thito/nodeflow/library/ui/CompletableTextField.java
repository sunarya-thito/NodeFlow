package thito.nodeflow.library.ui;

import javafx.beans.*;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.text.*;
import thito.nodeflow.api.node.*;
import thito.nodeflow.api.project.*;
import thito.nodeflow.library.binding.*;

import java.lang.ref.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

public class CompletableTextField extends TextField {

    private ContextMenu entriesPopup;
    private MethodParameterCompleter completer;
    private Project project;
    private Method method;
    private Parameter parameter;
    private ObservableList<String> suggestions = FXCollections.observableArrayList();
    private StringProperty selectedSuggestion = new SimpleStringProperty();

    public CompletableTextField(MethodParameterCompleter completer, Project project, Method method, Parameter parameter) {
        this.completer = completer;
        this.project = project;
        this.parameter = parameter;
        this.method = method;
        this.entriesPopup = new ContextMenu();
        this.entriesPopup.setAutoHide(false);
        this.entriesPopup.setHideOnEscape(false);
        entriesPopup.getStyleClass().add("completer-context-menu");
        initialize();
    }

    private void initialize() {
        MappedListBinding.bind(entriesPopup.getItems(), suggestions, text -> {
            MenuItem item = new MenuItem();
            item.getStyleClass().add("completer-suggestion-item");
            item.setGraphic(highlightText(text, getText()));
            textProperty().addListener(new WeakReferencedChangeListener<String>(new WeakReference<>(item)) {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    item.setGraphic(highlightText(text, newValue));
                }
            });
            item.setOnAction(event -> {
                event.consume();
                setText(text);
                positionCaret(text.length());
                hide();
            });
            return item;
        });
        selectedSuggestion.addListener(System.out::println);
        setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.TAB) {
                event.consume();
            }
        });

        refreshSuggestions(getText());
        textProperty().addListener((obs, old, val) -> {
            refreshSuggestions(val);
        });

        suggestions.addListener((InvalidationListener) obs -> {
            if (suggestions.isEmpty()) {
                hide();
            } else {
                show();
            }
        });

        focusedProperty().addListener((obs, old, val) -> {
            if (!val) {
                hide();
            } else if (!suggestions.isEmpty()) {
                show();
            }
        });
    }

    private void refreshSuggestions(String val) {
        if (val == null) {
            val = "";
        }
        String finalEnteredText = val;
        List<String> filteredEntries = completer.fetchValues(project, method, parameter).stream()
                .filter(e -> e.toLowerCase().contains(finalEnteredText.toLowerCase()))
                .collect(Collectors.toList());
        // intersect the list
        suggestions.retainAll(filteredEntries);
        filteredEntries.removeAll(suggestions);
        suggestions.addAll(filteredEntries);
    }

    private void show() {
        if (isFocused() && !entriesPopup.isShowing() && getScene() != null && getScene().getWindow() != null && getScene().getWindow().isShowing()) { //optional
            entriesPopup.show(this, Side.BOTTOM, 0, 0);
        }
    }

    private void hide() {
        entriesPopup.hide();
    }

    private static TextFlow highlightText(String text, String filter) {
        TextFlow flow = new TextFlow();
        int index;
        if (!filter.isEmpty()) {
            while ((index = text.indexOf(filter)) >= 0) {
                String highlightedText = text.substring(index, index + filter.length());
                if (index == 0) {
                    Text highlight = new Text(highlightedText);
                    highlight.getStyleClass().add("completer-highlighted-text");
                    flow.getChildren().add(highlight);
                } else {
                    Text before = new Text(text.substring(0, index));
                    before.getStyleClass().add("completer-normal-text");
                    Text highlight = new Text(highlightedText);
                    highlight.getStyleClass().add("completer-highlighted-text");
                    flow.getChildren().addAll(before, highlight);
                }
                text = text.substring(index + filter.length());
            }
        }
        if (text.length() > 0) {
            Text after = new Text(text);
            after.getStyleClass().add("completer-normal-text");
            flow.getChildren().add(after);
        }
        return flow;
    }

}