package thito.nodeflow.internal.ui.settings;

import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.collections.*;
import javafx.collections.transformation.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.internal.settings.*;
import thito.nodeflow.library.binding.*;
import thito.nodeflow.library.ui.Skin;
import thito.nodeflow.library.ui.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class SettingsSkin extends Skin {
    @Component("search")
    TextField search;

    @Component("category-view")
    TreeView<SettingsCategory> view;

    @Component("path")
    HBox breadcrumb;

    @Component("content")
    VBox content;

    private ObservableList<SettingsProperty<?>> items = FXCollections.observableArrayList();
    private FilteredList<SettingsProperty<?>> filteredList = new FilteredList<>(items);
    private SortedList<SettingsProperty<?>> sortedList = new SortedList<>(filteredList);
    private ObjectProperty<Predicate> filter = new SimpleObjectProperty<>();

    @Override
    protected void onLayoutLoaded() {
        view.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
            items.clear();
            if (val != null) {
                SettingsCategory value = val.getValue();
                if (value != null) {
                    items.addAll(value.getSettingsPropertyList());
                    List<SettingsCategory> path = new ArrayList<>();
                    while (value != null) {
                        path.add(0, value);
                        value = value.getParent();
                    }
                    breadcrumb.getChildren().setAll(path.stream().map(x -> new SettingsBreadcrumbSkin(x.getDescription().nameProperty())).collect(Collectors.toList()));
                }
            }
        });

        filteredList.predicateProperty().bind((ObservableValue) filter);

        MappedListBinding.bind(content.getChildren(), sortedList, SettingsItemSkin::new);
        TreeItem<SettingsCategory> categoryTreeItem = new TreeItem<>();
        MappedListBinding.bind(categoryTreeItem.getChildren(), NodeFlow.getInstance().getSettingsManager().getCategoryList(), SettingsCategoryItem::new);
        view.setRoot(categoryTreeItem);
        view.setShowRoot(false);
        view.setCellFactory(settingsCategoryTreeView -> new TreeCell<>() {
            @Override
            protected void updateItem(SettingsCategory category, boolean b) {
                super.updateItem(category, b);
                if (b) {
                    textProperty().unbind();
                    setText(null);
                } else {
                    textProperty().bind(category.getDescription().nameProperty());
                }
            }
        });
    }

    public class SettingsCategoryItem extends TreeItem<SettingsCategory> {
        private ObservableList<SettingsCategory> observableList;
        private FilteredList<SettingsCategory> filteredChildren;
        public SettingsCategoryItem(SettingsCategory category) {
            super(category);
            observableList = category.getSubCategory();
            filteredChildren = new FilteredList<>(observableList);
            filteredChildren.predicateProperty().bind((ObservableValue) filter);
            MappedListBinding.bind(getChildren(), filteredChildren, SettingsCategoryItem::new);
        }
    }
}
