package thito.nodeflow.api.event.settings;

import thito.nodeflow.api.event.Event;
import thito.nodeflow.api.ui.Theme;

public interface ThemeChangeEvent extends Event {
    Theme getTheme();

    void setTheme(Theme theme);

    Theme getPreviousTheme();
}
