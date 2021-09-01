package thito.nodeflow.internal.ui.settings;

import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.collections.*;
import javafx.collections.transformation.*;
import javafx.event.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.internal.settings.*;
import thito.nodeflow.library.binding.*;
import thito.nodeflow.library.ui.Skin;
import thito.nodeflow.library.ui.*;
import thito.nodeflow.library.util.*;

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

    @Component("ok")
    Button ok;

    @Component("cancel")
    Button cancel;

    @Component("apply")
    Button apply;

    private ObservableSet<SettingsItemSkin> changedList = FXCollections.observableSet();
    private ObservableList<SettingsProperty<?>> items = FXCollections.observableArrayList();
    private FilteredList<SettingsProperty<?>> filteredList = new FilteredList<>(items);
    private SortedList<SettingsProperty<?>> sortedList = new SortedList<>(filteredList);
    private ObjectProperty<Predicate> filter = new SimpleObjectProperty<>();
    private Map<SettingsProperty<?>, SettingsItemSkin> cached = new HashMap<>();

    @Override
    protected void initializeSkin() {
        super.initializeSkin();
        registerActionHandler("settings.ok", ActionEvent.ACTION, event -> {
            apply();
            SettingsWindow.closeWindow();
        });
        registerActionHandler("settings.cancel", ActionEvent.ACTION, event -> {
            SettingsWindow.closeWindow();
        });
        registerActionHandler("settings.apply", ActionEvent.ACTION, event -> {
            apply();
        });
    }

    @Override
    protected void onLayoutLoaded() {
        view.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
            items.clear();
            if (val == null) {
                view.getSelectionModel().selectFirst();
                return;
            }
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
        });

        filteredList.predicateProperty().bind((ObservableValue) filter);

        MappedListBinding.bind(content.getChildren(), sortedList, x -> cached.computeIfAbsent(x, a -> new SettingsItemSkin(this, a)));
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
        search.textProperty().addListener((obs, old, val) -> {
            if (val == null || val.trim().isEmpty()) {
                filter.set(null);
                return;
            }
            filter.set(obj -> {
                if (obj instanceof SettingsCategory) {
                    return matches((SettingsCategory) obj, val);
                }
                if (obj instanceof SettingsProperty) {
                    return Toolkit.searchScore(((SettingsProperty<?>) obj).displayNameProperty().get(), val) > 0 ||
                            Toolkit.searchScore(((SettingsProperty<?>) obj).getCategory().getDescription().nameProperty().get(), val) > 0;
                }
                return true;
            });
            expandAll(view.getRoot(), val, new ScoreCache());
        });

        view.getSelectionModel().selectFirst();

        apply.disableProperty().bind(Bindings.isEmpty(changedList));
    }

    public void apply() {
        for (SettingsItemSkin changed : new ArrayList<>(changedList)) {
            SettingsNode settingsNode = changed.getSettingsNode();
            if (settingsNode != null) {
                settingsNode.apply();
            }
        }
    }

    public ObservableSet<SettingsItemSkin> getChangedList() {
        return changedList;
    }

    class ScoreCache {
        double score;
        TreeItem<SettingsCategory> selected;
    }

    private void expandAll(TreeItem<SettingsCategory> item, String search, ScoreCache selectedItem) {
        if (item.getValue() != null) {
            double searchScore = calculateScore(item.getValue(), search);
            if (searchScore > selectedItem.score) {
                TreeItem<SettingsCategory> selected = selectedItem.selected;
                if (selected == null || selected.getValue() == null) {
                    view.getSelectionModel().select(item);
                    selectedItem.selected = item;
                    selectedItem.score = searchScore;
                }
            }
        }
        item.setExpanded(true);
        for (TreeItem<SettingsCategory> child : item.getChildren()) {
            expandAll(child, search, selectedItem);
        }
    }

    private static double calculateScore(SettingsCategory category, String search) {
        double score = Toolkit.searchScore(category.getDescription().nameProperty().get(), search);
        for (SettingsProperty prop : category.getSettingsPropertyList()) {
            score += Toolkit.searchScore(prop.displayNameProperty().get(), search);
        }
        return score;
    }

    private static boolean matches(SettingsCategory category, String search) {
        if (Toolkit.searchScore(category.getDescription().nameProperty().get(), search) > 0) {
            return true;
        }
        for (SettingsProperty prop : category.getSettingsPropertyList()) {
            if (Toolkit.searchScore(prop.displayNameProperty().get(), search) > 0) {
                return true;
            }
        }
        for (SettingsCategory sub : category.getSubCategory()) {
            if (matches(sub, search)) return true;
        }
        return false;
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
