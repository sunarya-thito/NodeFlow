package thito.nodeflow.library.ui.layout;

import javafx.animation.*;
import javafx.beans.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.util.Duration;
import thito.nodeflow.api.task.*;

public abstract class UIContainer extends UIComponent {

    private StackPane viewport;
    private StackPane layers;
    private UIContainerPreloader preloader;
    public UIContainer() {
        getChildren().add(layers());
        sceneProperty().addListener((obs, old, val) -> {
            if (val != null) {
                reloadContent();
            }
        });
    }

    // Lazy Load
    protected UIContainerPreloader preloader() {
        return preloader == null ? preloader = new UIContainerPreloader() : preloader;
    }
    private StackPane viewport() {
        return viewport == null ? viewport = new StackPane() : viewport;
    }
    private StackPane layers() {
        return layers == null ? layers = new StackPane(viewport(), preloader()) : layers;
    }
    //


    @Override
    protected void addComponent(Node node) {
        viewport().getChildren().add(node);
    }

    @Override
    protected void onLayoutChange(Observable observable, Layout old, Layout value) {
        if (value != null) {
            try {
                fillOutFields();
                value.getParser().parseLayout(value.getDocument(), this);
                fieldMap = null;
                onLayoutReady();
            } catch (LayoutParserException e) {
                e.printStackTrace();
            }
        } else {
            viewport().getChildren().clear();
        }
    }

    public void reloadContent() {
        Task.runOnForeground("pre-load", this::preLoadContent);
    }

    // Load Stages
    private long fadeOutDuration;
    protected void preLoadContent() {
        fadeOutDuration = 500;
        showPreloader();
        Task.runOnBackground("load", this::loadContent);
    }

    protected void loadContent() {
        long time = System.currentTimeMillis();
        initializeContent();
        long elapsed = System.currentTimeMillis() - time;
        if (elapsed < 500) {
            // If the page content loaded less than 500ms (fast/no content loaded)
            // then we skip the fade out duration.
            // 500ms is the duration of page transition
            fadeOutDuration = 0;
        }
        Task.runOnForeground("post-load", this::postLoadContent);
    }

    protected void postLoadContent() {
        hidePreloader();
    }
    //

    private void showPreloader() {
        preloader().getViewport().setOpacity(1);
        preloader().setMouseTransparent(false);
        preloader().setOpacity(1);
    }

    private Timeline hidePreloaderAnimation;
    private void hidePreloader() {
        if (hidePreloaderAnimation != null) {
            hidePreloaderAnimation.stop();
        }
        if (fadeOutDuration <= 0) {
            preloader().setOpacity(0);
            preloader().setMouseTransparent(true);
            return;
        }
        hidePreloaderAnimation = new Timeline(
                new KeyFrame(Duration.millis(fadeOutDuration),
                        new KeyValue(preloader().getViewport().opacityProperty(), 0))
        );
        hidePreloaderAnimation.setOnFinished(event -> {
            hidePreloaderAnimation = new Timeline(
                    new KeyFrame(Duration.millis(fadeOutDuration),
                            new KeyValue(preloader().opacityProperty(), 0),
                            new KeyValue(preloader().mouseTransparentProperty(), true))
            );
            hidePreloaderAnimation.play();
        });
        hidePreloaderAnimation.play();
    }

    protected abstract void initializeContent();
}
