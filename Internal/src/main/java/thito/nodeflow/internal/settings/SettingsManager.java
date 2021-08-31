package thito.nodeflow.internal.settings;

import javafx.collections.*;
import thito.nodeflow.internal.plugin.*;
import thito.nodeflow.internal.plugin.event.application.*;
import thito.nodeflow.internal.settings.node.*;
import thito.nodeflow.internal.settings.parser.*;
import thito.nodeflow.library.config.*;
import thito.nodeflow.library.language.*;
import thito.nodeflow.library.ui.*;

import java.io.*;
import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.logging.*;

public class SettingsManager {
    private static Logger logger = Logger.getLogger("Settings");
    private ObservableList<SettingsCategory> categoryList = FXCollections.observableArrayList();
    private Map<Class<?>, SettingsParser<?>> parserMap = new HashMap<>();
    private Map<Class<?>, SettingsNodeFactory<?>> nodeMap = new HashMap<>();
    private Section loaded;

    public SettingsManager() {
        registerParser(Boolean.class, new BooleanParser());
        registerParser(Byte.class, new ByteParser());
        registerParser(Character.class, new CharParser());
        registerParser(Double.class, new DoubleParser());
        registerParser(File.class, new FileParser());
        registerParser(Float.class, new FloatParser());
        registerParser(Integer.class, new IntegerParser());
        registerParser(Language.class, new LanguageParser());
        registerParser(Long.class, new LongParser());
        registerParser(Short.class, new ShortParser());
        registerParser(Theme.class, new ThemeParser());
        registerParser(String.class, new StringParser());

        registerNodeFactory(Boolean.class, new BooleanNode.Factory());
        registerNodeFactory(Byte.class, new NumberNode.Factory());
        registerNodeFactory(Character.class, new CharacterNode.Factory());
        registerNodeFactory(Double.class, new NumberNode.Factory());
        registerNodeFactory(File.class, new FileNode.Factory());
        registerNodeFactory(Float.class, new NumberNode.Factory());
        registerNodeFactory(Integer.class, new NumberNode.Factory());
        registerNodeFactory(Language.class, new SelectionNode.LanguageFactory());
        registerNodeFactory(Long.class, new NumberNode.Factory());
        registerNodeFactory(Short.class, new NumberNode.Factory());
        registerNodeFactory(Theme.class, new SelectionNode.ThemeFactory());
        registerNodeFactory(String.class, new StringParser.Factory());
    }

    public <T extends Settings> Optional<T> getSettings(Class<T> category) {
        for (SettingsCategory settingsCategory : categoryList) {
            T found = findSettings(settingsCategory, category);
            if (found != null) return Optional.of(found);
        }
        return Optional.empty();
    }

    private <T extends Settings> T findSettings(SettingsCategory category, Class<T> type) {
        if (category.getType().equals(type)) return (T) category.getSettings();
        if (category != null) {
            for (SettingsCategory sub : category.subCategory) {
                T found = findSettings(sub, type);
                if (found != null) return found;
            }
        }
        return null;
    }

    @SafeVarargs
    public final void addCategories(Class<? extends Settings>... rootCategories) {
        for (Class<? extends Settings> category : rootCategories) {
            try {
                SettingsCategory c = scan(null, null, category);
                categoryList.add(c);
            } catch (Exception e) {
                logger.log(Level.WARNING, "Failed to scan category "+category.getName(), e);
            }
        }
    }

    public ObservableList<SettingsCategory> getCategoryList() {
        return categoryList;
    }

    public <T> SettingsNodeFactory<? super T> getNodeFactory(Class<T> clazz) {
        return (SettingsNodeFactory<? super T>) nodeMap.get(clazz);
    }

    public <T> void registerParser(Class<T> clazz, SettingsParser<? extends T> parser) {
        parserMap.put(clazz, parser);
    }

    public <T> void registerNodeFactory(Class<T> clazz, SettingsNodeFactory<? super T> factory) {
        nodeMap.put(clazz, factory);
    }

    public void unregisterParser(SettingsParser<?> parser) {
        parserMap.values().remove(parser);
    }

    public void unregisterNodeFactory(SettingsNodeFactory<?> factory) {
        nodeMap.values().remove(factory);
    }

    public void load(Reader reader) {
        loaded = Section.parseToMap(reader);
        load(null, categoryList);
    }

    public void save(Writer writer) throws IOException {
        save(null, categoryList);
        writer.write(Section.toString(loaded));
    }

    private void save(String parent, List<SettingsCategory> child) {
        for (SettingsCategory category : child) {
            String path = parent == null ? category.getClass().getSimpleName() : parent + "." + category.getClass().getSimpleName();
            for (SettingsProperty property : category.settingsPropertyList) {
                String itemPath = path + "." + property.fieldName;
                SettingsParser parser = parserMap.get(property.getType());
                if (parser == null) {
                    logger.log(Level.WARNING, "SettingsProperty "+itemPath.replace('.', '/')+" does not have any parser for "+property.getType().getName());
                    continue;
                }
                SettingsSaveEvent event = new SettingsSaveEvent(property);
                PluginManager.getPluginManager().fireEvent(event);
                parser.toConfig(loaded, itemPath, property.get());
            }
            save(path, category.subCategory);
        }
    }

    private void load(String parent, List<SettingsCategory> child) {
        for (SettingsCategory category : child) {
            String path = parent == null ? category.getClass().getSimpleName() : parent + "." + category.getClass().getSimpleName();
            for (SettingsProperty property : category.settingsPropertyList) {
                String itemPath = path + "." + property.fieldName;
                SettingsPreLoadEvent settingsPreLoadEvent = new SettingsPreLoadEvent(property);
                PluginManager.getPluginManager().fireEvent(settingsPreLoadEvent);
                if (settingsPreLoadEvent.isCancelled()) continue;
                SettingsParser parser = parserMap.get(property.getType());
                if (parser == null) {
                    logger.log(Level.WARNING, "SettingsProperty "+itemPath.replace('.', '/')+" does not have any parser for "+property.getType().getName());
                    continue;
                }
                parser.fromConfig(loaded, itemPath).ifPresent(property::set);
                SettingsLoadEvent settingsLoadEvent = new SettingsLoadEvent(property);
                PluginManager.getPluginManager().fireEvent(settingsLoadEvent);
            }
            load(path, category.subCategory);
        }
    }

    protected SettingsCategory scan(Settings parentSettings, SettingsCategory parentCategory, Class<? extends Settings> settingsClass)
            throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<?> constructor = settingsClass.getDeclaredConstructors()[0];
        if (constructor == null) throw new IllegalArgumentException("no default constructor for "+settingsClass.getName());
        constructor.setAccessible(true);
        Settings settings = (Settings) (parentSettings != null && !Modifier.isStatic(settingsClass.getModifiers()) ?
                constructor.newInstance(parentSettings) :
                constructor.newInstance());
        SettingsCategory category = new SettingsCategory(settingsClass, settings);
        category.parent = parentCategory;
        Class<?> parent = settingsClass;
        while (Settings.class.isAssignableFrom(parent)) {
            for (Field field : parent.getDeclaredFields()) {
                field.setAccessible(true);
                if (SettingsProperty.class.isAssignableFrom(field.getType())) {
                    SettingsProperty<?> item = (SettingsProperty<?>) field.get(settings);
                    item.category = category;
                    Type rawType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                    if (!(rawType instanceof Class<?>)) throw new IllegalArgumentException("invalid SettingsProperty for "+field.toGenericString());
                    for (Annotation annotation : field.getAnnotations()) {
                        item.annotationMap.put(annotation.annotationType(), annotation);
                    }
                    item.type = (Class) rawType;
                    item.fieldName = field.getName();
                    category.settingsPropertyList.add(item);
                }
            }
            parent = parent.getSuperclass();
        }
        for (Class<?> clazz : settings.getClass().getClasses()) {
            if (Settings.class.isAssignableFrom(clazz)) {
                try {
                    SettingsCategory subCategory = scan(settings, category, clazz.asSubclass(Settings.class));
                    category.subCategory.add(subCategory);
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Failed to scan category "+clazz.getName(), e);
                }
            }
        }
        return category;
    }
}
