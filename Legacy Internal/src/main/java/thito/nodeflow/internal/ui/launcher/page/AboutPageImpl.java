package thito.nodeflow.internal.ui.launcher.page;

import javafx.scene.*;
import thito.nodeflow.api.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.ui.launcher.page.*;
import thito.nodeflow.internal.ui.launcher.*;
import thito.nodeflow.library.ui.decoration.*;

public class AboutPageImpl extends AbstractLauncherPage implements AboutPage {
    public AboutPageImpl() {
        super(I18n.$("launcher-button-about"), I18n.$("launcher-page-about"),
                null,
                NodeFlow.getApplication().getResourceManager().getIcon("launcher-button-about"));
        setFooterEnabled(false);
        setHeaderEnabled(false);
    }

    private int impl_hearts, impl_scores;

    @Override
    public int getScores() {
        return impl_scores;
    }

    @Override
    public int getHearts() {
        return impl_hearts;
    }

    @Override
    protected Node requestViewport() {
        AboutScreen screen = new AboutScreen() {
            @Override
            protected void setTotalScore(int score) {
                impl_scores = score;
                super.setTotalScore(score);
            }

            @Override
            protected void onHeartChange(int heart) {
                impl_hearts = heart;
            }
        };
        return screen;
    }
}
