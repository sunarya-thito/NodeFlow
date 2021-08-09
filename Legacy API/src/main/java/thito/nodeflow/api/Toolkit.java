package thito.nodeflow.api;

import thito.nodeflow.api.action.Action;
import thito.nodeflow.api.config.ListSection;
import thito.nodeflow.api.config.MapEditor;
import thito.nodeflow.api.config.MapSection;
import thito.nodeflow.api.config.Section;
import thito.nodeflow.api.java.JavaType;
import thito.nodeflow.api.ui.RadioButtonGroup;
import thito.nodeflow.api.ui.menu.MenuItem;
import thito.nodeflow.api.ui.menu.MenuItemType;
import thito.nodeflow.api.ui.menu.type.ButtonMenuItemType;
import thito.nodeflow.api.ui.menu.type.CheckBoxMenuItemType;
import thito.nodeflow.api.ui.menu.type.RadioButtonMenuItemType;

import java.io.Reader;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.logging.*;

public interface Toolkit {

    static void log(Object msg) {
        NodeFlow.getApplication().getLogger().log(Level.INFO, String.valueOf(msg));
    }

    void saveYaml(Section section, Writer writer);

    Section loadYaml(Reader reader);

    MapSection newDefaultMapSection();

    ListSection newDefaultListSection();

    MapSection newMapSection(Map<?, ?> map);

    ListSection newListSection(List<?> list);

    MapSection newMapSection();

    ListSection newListSection(Object... elements);

    ListSection newListSection();

    <M extends Map<K, V>, K, V> MapEditor<M, K, V> newMapEditor(M map);

    MenuItem createItem(Action action, MenuItemType type);

    ButtonMenuItemType menuButtonType();

    CheckBoxMenuItemType menuCheckBoxType();

    RadioButtonMenuItemType menuRadioButtonType();

    MenuItem createSeparatorItem();

    RadioButtonGroup createGroup(String name, int maxAmount);
}
