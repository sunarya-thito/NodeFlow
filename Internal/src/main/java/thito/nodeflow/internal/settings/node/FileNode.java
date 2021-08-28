package thito.nodeflow.internal.settings.node;

import javafx.beans.property.*;
import javafx.css.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import thito.nodeflow.internal.settings.*;
import thito.nodeflow.library.language.*;

import java.io.*;
import java.lang.annotation.*;
import java.util.*;
import java.util.stream.*;

public class FileNode extends SettingsNode<File> {

    public static final FileSettings DEFAULT_SETTINGS = new FileSettings() {
        @Override
        public boolean mustExist() {
            return false;
        }

        @Override
        public boolean directory() {
            return false;
        }

        @Override
        public boolean save() {
            return false;
        }

        @Override
        public String[] filters() {
            return new String[] {"Any *"};
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return FileSettings.class;
        }
    };
    public static class Factory implements SettingsNodeFactory<File> {
        @Override
        public SettingsNode<File> createNode(SettingsProperty<File> item) {
            FileSettings settings = item.getAnnotated(FileSettings.class);
            if (settings == null) settings = DEFAULT_SETTINGS;
            return new FileNode(item, settings.mustExist(), settings.directory(), settings.save(), Arrays.stream(settings.filters()).map(x -> {
                String[] split = x.split(" ");
                FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(split[0], split[1].split(","));
                return filter;
            }).collect(Collectors.toList()));
        }
    }

    private static final PseudoClass invalid = PseudoClass.getPseudoClass("invalid");
    private HBox box;
    boolean updating;
    private boolean directory, save;
    private List<FileChooser.ExtensionFilter> filters;
    private ObjectProperty<File> selectedFile = new SimpleObjectProperty<>();
    public FileNode(SettingsProperty<File> item, boolean mustExist, boolean directory, boolean save, List<FileChooser.ExtensionFilter> extensionFilters) {
        super(item);
        filters = extensionFilters;
        this.directory = directory;
        this.save = save;
        TextField field = new TextField();
        Button button = new Button();
        button.textProperty().bind(I18n.$("browse"));
        selectedFile.set(item.get().getAbsoluteFile());
        field.textProperty().set(item.get().getAbsolutePath());
        field.textProperty().addListener((obs, old, val) -> {
            if (updating) return;
            File file = new File(val).getAbsoluteFile();
            if (mustExist && !file.exists()) {
                field.pseudoClassStateChanged(invalid, true);
                return;
            }
            field.pseudoClassStateChanged(invalid, false);
            updating = true;
            selectedFile.set(file);
            updating = false;
        });
        selectedFile.addListener((obs, old, val) -> {
            if (updating) return;
            updating = true;
            field.setText(val.getAbsolutePath());
            updating = false;
        });
        button.setOnAction(event -> {
            browse();
        });
        box = new HBox(field, button);
        box.getStyleClass().add("settings-file");

        hasChangedPropertyProperty().bind(selectedFile.isNotEqualTo(item));
    }

    @Override
    public void apply() {
        getItem().set(selectedFile.get());
    }

    private File getExistingRoot() {
        File current = getItem().get();
        while (current != null) {
            if (current.isDirectory() && current.exists()) return current;
            current = current.getParentFile();
        }
        return null;
    }

    public void browse() {
        if (directory) {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setInitialDirectory(getExistingRoot());
            chooser.titleProperty().bind(I18n.$("open-directory"));
            chooser.showDialog(box.getScene().getWindow());
        } else {
            FileChooser chooser = new FileChooser();
            chooser.setInitialDirectory(getExistingRoot());
            if (filters != null && !filters.isEmpty()) {
                chooser.getExtensionFilters().addAll(filters);
                chooser.setSelectedExtensionFilter(filters.get(0));
            }
            chooser.titleProperty().bind(I18n.$("open-file"));
            chooser.setInitialFileName(getItem().get().getName());
            File result;
            if (save) {
                result = chooser.showSaveDialog(box.getScene().getWindow());
            } else {
                result = chooser.showOpenDialog(box.getScene().getWindow());
            }
            selectedFile.set(result);
        }
    }

    @Override
    public Node getNode() {
        return box;
    }
}
