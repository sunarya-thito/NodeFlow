package thito.nodeflow.internal.ui.launcher.page;

import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.ui.launcher.*;

public class PageDisableReasonImpl implements PageDisableReason {
    private I18nItem title, message;

    public PageDisableReasonImpl(I18nItem title, I18nItem message) {
        this.title = title;
        this.message = message;
    }

    @Override
    public I18nItem getTitle() {
        return title;
    }

    @Override
    public I18nItem getMessage() {
        return message;
    }
}
