package thito.nodeflow.internal.ui.dialog.content.form;

import com.sun.javafx.scene.control.skin.*;
import javafx.application.*;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.collections.transformation.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.util.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.ui.dialog.content.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.library.binding.*;

import java.util.*;

public class ChoiceFormImpl<T> extends AbstractFormImpl<T> implements FormContent.ChoiceForm<T> {

    private static <T> T first(List<T> list) {
        if (list.isEmpty()) return null;
        return list.get(0);
    }

    private ObservableList<T> choices = FXCollections.observableArrayList();
    private ObjectProperty<StringConverter<T>> converter = new SimpleObjectProperty<>(Toolkit.STRING_CONVERTER);
    private BooleanProperty disable = new SimpleBooleanProperty();
    public ChoiceFormImpl(I18nItem question, List<T> choices, boolean optional) {
        this(question, first(choices), choices, optional);
    }

    public ChoiceFormImpl(I18nItem question, T initialValue, List<T> choices, boolean optional) {
        super(question, initialValue, optional);
        this.choices.addAll(choices);
    }

    @Override
    public BooleanProperty impl_disableProperty() {
        return disable;
    }

    @Override
    public List<T> getChoices() {
        return Collections.unmodifiableList(choices);
    }

    @Override
    public Node createFieldPeer() {
        ComboBox<HideableItem<T>> comboBox = createComboBoxWithAutoCompletionSupport(choices);
        comboBox.disableProperty().bind(disable);
        comboBox.getStyleClass().add("form-choices");
        comboBox.setConverter(new StringConverter<HideableItem<T>>() {
            @Override
            public String toString(HideableItem<T> object) {
                return converter.get().toString(object == null ? null : object.getObject());
            }

            @Override
            public HideableItem<T> fromString(String string) {
                T from = converter.get().fromString(string);
                if (from == null) {
                    for (HideableItem<T> item : comboBox.getItems()) {
                        if (Objects.equals(toString(item), string)) {
                            return item;
                        }
                    }
                    for (HideableItem<T> item : comboBox.getItems()) {
                        if (comboBox.getConverter().toString(item).toLowerCase().startsWith(string)) {
                            return item;
                        }
                    }
                }
                for (HideableItem<T> item : comboBox.getItems()) {
                    if (item != null && item.getObject() == from) {
                        return item;
                    }
                }
                return null;
            }
        });
        MappedBidirectionalBinding.bindBidirectional(comboBox.valueProperty(), impl_answerProperty(), x -> x == null ? null : x.getObject(),
                x -> {
                    for (HideableItem<T> item : comboBox.getItems()) {
                        if (item != null && item.getObject() == x) {
                            return item;
                        }
                    }
                    return null;
                });
        return comboBox;
    }

    @Override
    public StringConverter<T> getStringConverter() {
        return converter.get();
    }

    @Override
    public void setStringConverter(StringConverter<T> converter) {
        this.converter.set(converter);
    }

    public static class HideableItem<T>
    {
        private int score;
        private final ObjectProperty<T> object = new SimpleObjectProperty<>();
        private final BooleanProperty hidden = new SimpleBooleanProperty();

        private HideableItem(T object)
        {
            setObject(object);
        }

        private ObjectProperty<T> objectProperty(){return this.object;}
        private T getObject(){return this.objectProperty().get();}
        private void setObject(T object){this.objectProperty().set(object);}

        private BooleanProperty hiddenProperty(){return this.hidden;}
        private boolean isHidden(){return this.hiddenProperty().get();}
        private void setHidden(boolean hidden){this.hiddenProperty().set(hidden);}

        @Override
        public String toString()
        {
            return getObject() == null ? null : getObject().toString();
        }
    }
    private static <T> ComboBox<HideableItem<T>> createComboBoxWithAutoCompletionSupport(List<T> items)
    {
        ObservableList<HideableItem<T>> hideableHideableItems = FXCollections.observableArrayList(hideableItem -> new Observable[]{hideableItem.hiddenProperty()});

        items.forEach(item ->
        {
            HideableItem<T> hideableItem = new HideableItem<>(item);
            hideableHideableItems.add(hideableItem);
        });

        FilteredList<HideableItem<T>> filteredHideableItems = new FilteredList<>(hideableHideableItems, t -> !t.isHidden());

        ComboBox<HideableItem<T>> comboBox = new ComboBox<>();
        comboBox.setItems(filteredHideableItems);

        HideableItem<T>[] selectedItem = (HideableItem<T>[]) new HideableItem[1];

        comboBox.addEventHandler(KeyEvent.KEY_PRESSED, event ->
        {
            if (!comboBox.isShowing() || event.isShiftDown() || event.isControlDown())
            {
                return;
            }
            comboBox.setEditable(true);
            comboBox.getEditor().clear();
        });
        comboBox.editableProperty().addListener((obs, old, val) -> {
            if (!val) {
                HideableItem<T> result = comboBox.getConverter().fromString(comboBox.getEditor().textProperty().get());
                Platform.runLater(() -> {
                    comboBox.getSelectionModel().select(result);
                    comboBox.setValue(result);
                });
            }
        });
        comboBox.showingProperty().addListener((observable, oldValue, newValue) ->
        {
            if(newValue) {
                ListView<HideableItem> lv = ((ComboBoxListViewSkin<HideableItem>) comboBox.getSkin()).getListView();

                Platform.runLater(() -> {
                    if(selectedItem[0] == null) // first use
                    {
                        double cellHeight = ((Control) lv.lookup(".list-cell")).getHeight();
                        lv.setFixedCellSize(cellHeight);
                    }
                });

                lv.scrollTo(comboBox.getValue());
            } else {
                HideableItem<T> value = comboBox.getValue();
                if (value != null) selectedItem[0] = value;

                comboBox.setEditable(false);
            }
        });

        comboBox.setOnHidden(event -> hideableHideableItems.forEach(item -> item.setHidden(false)));

        comboBox.getEditor().textProperty().addListener((obs, oldValue, newValue) ->
        {
            if(!comboBox.isShowing()) return;
            hideableHideableItems.sort((a, b) -> {
                String aStr, bStr;
                int bRes = Toolkit.calculateSearchScore(aStr = comboBox.getConverter().toString(b), newValue);
                int aRes = Toolkit.calculateSearchScore(bStr = comboBox.getConverter().toString(a), newValue);
                b.setHidden(bRes <= 0);
                a.setHidden(aRes <= 0);
                if (aRes == bRes) {
                    return bStr.compareToIgnoreCase(aStr);
                }
                return bRes - aRes;
            });
        });

        return comboBox;
    }
}
