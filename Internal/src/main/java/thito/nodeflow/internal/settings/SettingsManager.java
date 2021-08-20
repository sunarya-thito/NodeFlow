package thito.nodeflow.internal.settings;

import javafx.collections.*;
import thito.nodeflow.internal.plugin.*;
import thito.nodeflow.internal.plugin.event.application.*;
import thito.nodeflow.internal.settings.parser.*;
import thito.nodeflow.internal.ui.*;
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
    private StandardWindow window = new StandardWindow();
    private ObservableList<SettingsCategory> categoryList = FXCollections.observableArrayList();
    private Map<Class<?>, SettingsParser<?>> parserMap = new HashMap<>();
    private Section loaded;

    public SettingsManager(Class<? extends Settings>... rootCategories) {
        for (Class<? extends Settings> category : rootCategories) {
            try {
                SettingsCategory c = scan(null, category);
                categoryList.add(c);
            } catch (Exception e) {
                logger.log(Level.WARNING, "Failed to scan category "+category.getName(), e);
            }
        }

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
    }

    public void registerParser(Class<?> clazz, SettingsParser<?> parser) {
        parserMap.put(clazz, parser);
    }

    public void load(Reader reader) {
        loaded = Section.parseToMap(reader);
        load(null, categoryList);
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
                property.set(parser.fromConfig(loaded, itemPath));
                SettingsLoadEvent settingsLoadEvent = new SettingsLoadEvent(property);
                property.set(settingsLoadEvent);
            }
            load(path, category.subCategory);
        }
    }

    public StandardWindow getWindow() {
        return window;
    }

    private SettingsCategory scan(Settings parentCategory, Class<? extends Settings> settingsClass)
            throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<?> constructor = settingsClass.asSubclass(Object.class).getConstructors()[0];
        Settings settings = (Settings) (parentCategory != null && !Modifier.isStatic(settingsClass.getModifiers()) ?
                constructor.newInstance(parentCategory) :
                constructor.newInstance());
        SettingsCategory category = new SettingsCategory(settings.description());
        Class<?> parent = settingsClass;
        while (Settings.class.isAssignableFrom(parent)) {
            for (Field field : parent.getDeclaredFields()) {
                field.setAccessible(true);
                if (SettingsProperty.class.isAssignableFrom(field.getType())) {
                    SettingsProperty<?> item = (SettingsProperty<?>) field.get(settings);
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
                    SettingsCategory subCategory = scan(settings, clazz.asSubclass(Settings.class));
                    category.subCategory.add(subCategory);
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Failed to scan category "+clazz.getName(), e);
                }
            }
        }
        return category;
    }
}
