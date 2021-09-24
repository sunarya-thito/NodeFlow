package thito.nodeflow.internal.settings;

import thito.nodeflow.internal.language.*;
import thito.nodeflow.internal.settings.canvas.*;
import thito.nodeflow.internal.settings.parser.*;
import thito.nodeflow.internal.settings.ui.*;
import thito.nodeflow.internal.ui.*;

import java.io.*;
import java.util.*;

public class SettingsManager {
    private static SettingsManager settingsManager = new SettingsManager();

    public static SettingsManager getSettingsManager() {
        return settingsManager;
    }

    private List<Class<? extends SettingsCanvas>> settingsCanvasList = new ArrayList<>();
    private Map<Class<?>, SettingsParser<?>> parserMap = new HashMap<>();
    private Map<Class<?>, SettingsComponentFactory<?>> componentFactoryMap = new HashMap<>();

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
    }

    public <T> void registerComponentFactory(Class<T> type, SettingsComponentFactory<T> componentFactory) {
        componentFactoryMap.put(type, componentFactory);
    }

    public <T> void unregisterComponentFactory(Class<T> type, SettingsComponentFactory<T> componentFactory) {
        componentFactoryMap.remove(type, componentFactory);
    }

    public <T> SettingsComponentFactory<T> getComponentFactory(Class<T> type) {
        return (SettingsComponentFactory<T>) componentFactoryMap.remove(type);
    }

    public <T> SettingsParser<T> getParser(Class<T> type) {
        return (SettingsParser<T>) parserMap.get(type);
    }

    public List<Class<? extends SettingsCanvas>> getSettingsCanvasList() {
        return settingsCanvasList;
    }

    public void registerCanvas(Class<? extends SettingsCanvas> canvasType) {
        ReflectedSettingsCategory c = SettingsCanvas.readCanvas(canvasType);
        settingsCanvasList.add(canvasType);
        Settings.getSettings().registerSettingsCategory(c);
    }

    public void unregisterCanvas(Class<? extends SettingsCanvas> canvasType) {
        Category category = canvasType.getAnnotation(Category.class);
        if (category == null) throw new IllegalArgumentException("not a proper canvas, Category annotation is missing!");
        settingsCanvasList.remove(canvasType);
        Settings.getSettings().unregisterSettingsCanvas(canvasType);
    }

    public <T> void registerParser(Class<T> type, SettingsParser<T> parser) {
        parserMap.put(type, parser);
    }

    public <T> void unregisterParser(Class<T> type, SettingsParser<T> parser) {
        parserMap.remove(type, parser);
    }
}
