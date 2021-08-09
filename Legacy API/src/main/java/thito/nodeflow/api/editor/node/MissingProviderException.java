package thito.nodeflow.api.editor.node;

import thito.nodeflow.api.locale.*;

public class MissingProviderException extends Exception {
    private String providerId;
    public MissingProviderException(String providerId) {
        super(I18n.$("missing-provider").getString(providerId));
        this.providerId = providerId;
    }

    public String getProviderId() {
        return providerId;
    }
}
