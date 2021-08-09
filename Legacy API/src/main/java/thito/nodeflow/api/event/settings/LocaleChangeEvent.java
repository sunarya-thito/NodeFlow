package thito.nodeflow.api.event.settings;

import thito.nodeflow.api.event.Event;

import java.util.Locale;

public interface LocaleChangeEvent extends Event {
    Locale getLocale();

    void setLocale(Locale locale);

    Locale getPreviousLocale();
}
