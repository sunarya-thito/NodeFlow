package thito.nodeflow.internal.ui;

import javafx.animation.*;
import javafx.application.*;
import javafx.beans.value.*;
import javafx.collections.*;
import javafx.concurrent.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.web.*;
import javafx.stage.*;
import javafx.util.Duration;
import netscape.javascript.*;
import thito.jdhp.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.task.Task;
import thito.nodeflow.internal.*;

import java.lang.reflect.*;
import java.util.*;

public class DocsUI extends SimpleContextMenu {
    private String style =
            "body {" +
                    "background-color: rgb(50, 50, 50);" +
                    "}" +
                    "body * {" +
                    "color: rgb(200, 200, 200);" +
                    "font-family: Arial;" +
                    "}";
    private BorderPane viewport;
    private Member member;
    private Class<?> type;
    private JDProperties properties;
    private Timeline timeline;
    private boolean allowAdjustingHeight = true;
    private Stage owner;
    public DocsUI(Stage owner, Member member) {
        super(owner);
        this.owner = owner;
        this.member = member;
        initialize();
    }

    private static Class<?> generalize(Class<?> c) {
        if (c == null) return Object.class;
        if (c.isArray()) {
            return generalize(c.getComponentType());
        }
        return c;
    }

    public DocsUI(Stage owner, Class<?> type) {
        super(owner);
        this.owner = owner;
        this.type = generalize(type);
        initialize();
    }

    public void setBgColor(int r, int g, int b) {
        style = "body {" +
                "background-color: rgb("+r+", "+g+", "+b+");" +
                "}" +
                "body * {" +
                "color: rgb(200, 200, 200);" +
                "font-family: Arial;" +
                "}";
    }

    @Override
    protected void attemptUnfocusClose() {
    }

    public void setAllowAdjustingHeight(boolean allowAdjustingHeight) {
        this.allowAdjustingHeight = allowAdjustingHeight;
        viewport.setMouseTransparent(allowAdjustingHeight);
    }

    private void tick() {
        if (content != null) {
            content.adjustHeight();
        }
    }

    public BorderPane getViewport() {
        return viewport;
    }

    private void initialize() {
        timeline = new Timeline(new KeyFrame(Duration.millis(100), e -> tick()));
        timeline.setCycleCount(-1);
        viewport = new BorderPane();
        viewport.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
        getStage().getScene().setFill(Color.TRANSPARENT);
        viewport.setCenter(new OverlayUI(I18n.$("loading")));
        viewport.setPrefHeight(300);
        viewport.setMinWidth(500);
        getStage().getScene().getRoot().setMouseTransparent(true);
        getStage().getScene().addEventHandler(KeyEvent.ANY, Event::consume);
        getStage().getScene().addEventFilter(KeyEvent.ANY, Event::consume);
        getStage().setOnShown(event -> owner.requestFocus());
        setViewport(viewport);
        Task.runOnBackground("load-docs", this::loadContent);
    }

    public void loadContent() {
        ClassLoader loader;
        if (member != null) {
            loader = member.getDeclaringClass().getClassLoader();
        } else {
            loader = type.getClassLoader();
        }
        JDHub hub;
        if (loader instanceof DocumentedClassLoader) {
            hub = JDHub.createOnlineHub(((DocumentedClassLoader) loader).getDocsURL(), ((DocumentedClassLoader) loader).getDocsVersion());
        } else if (loader == null) {
            hub = JDHub.createOracleJDHub(8);
        } else {
            Task.runOnForeground("render-docs", this::renderContent);
            return;
        }
        try {
            if (properties.getHtml() != null && !properties.getHtml().isEmpty() && !properties.getHtml().equals("null")) {
                if (member instanceof Constructor) {
                    properties = hub.findConstructor((Constructor<?>) member);
                    properties.setHtml("<h4>From " + member.getDeclaringClass().getName() + "</h4>" + properties.getHtml());
                } else if (member instanceof Method) {
                    properties = hub.findMethod((Method) member);
                    properties.setHtml("<h4>From " + member.getDeclaringClass().getName() + "</h4>" + properties.getHtml());
                } else if (member instanceof Field) {
                    properties = hub.findField((Field) member);
                    properties.setHtml("<h4>From " + member.getDeclaringClass().getName() + "</h4>" + properties.getHtml());
                } else {
                    properties = hub.findClass(type);
                    properties.setHtml("<h3>" + type.getName() + "</h3>" + properties.getHtml());
                }
            }
        } catch (Throwable t) {
            Task.runOnForeground("hide-docs", this::hide);
            return;
        }
        Task.runOnForeground("render-docs", this::renderContent);
    }

    @Override
    public void show() {
        super.show();
        timeline.play();
    }

    @Override
    public void hide() {
        timeline.stop();
        super.hide();
    }

    @Override
    public void show(double x, double y) {
        super.show(x, y);
        timeline.play();
    }

    private WebViewFitContent content;
    private void renderContent() {
        if (properties == null) {
            return;
        }
        if (properties.getHtml() == null || properties.getHtml().isEmpty() || properties.getHtml().equals("null")) {
            return;
        }
        content = new WebViewFitContent("<style>"+style+"</style>"+properties.getHtml()+"");
        viewport.setCenter(content);
        ((Pane) getStage().getScene().getRoot()).setMaxHeight(-1);
    }

    public final class WebViewFitContent extends StackPane {

        final WebView webview = new WebView();
        final WebEngine webEngine = webview.getEngine();

        public WebViewFitContent(String content) {
            setPrefHeight(0);
            webview.setPrefHeight(5);
            if (allowAdjustingHeight) {
                widthProperty().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        Double width = (Double)newValue;
                        webview.setPrefWidth(width);
                        adjustHeight();
                    }
                });

                webview.getEngine().getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
                    @Override
                    public void changed(ObservableValue<? extends Worker.State> arg0, Worker.State oldState, Worker.State newState)         {
                        if (newState == Worker.State.SUCCEEDED) {
                            adjustHeight();
                        }
                    }
                });

                webview.getChildrenUnmodifiable().addListener(new ListChangeListener<Node>() {
                    @Override
                    public void onChanged(ListChangeListener.Change<? extends Node> change) {
                        Set<Node> scrolls = webview.lookupAll(".scroll-bar");
                        for (Node scroll : scrolls) {
                            scroll.setVisible(false);
                        }
                    }
                });
            }

            setContent(content);
            webview.setContextMenuEnabled(false);
            getChildren().add(webview);
        }

        public void setContent(final String content) {
            Platform.runLater(() -> {
                webEngine.loadContent(getHtml(content));
                Platform.runLater(() -> adjustHeight());
            });
        }


        @Override
        protected void layoutChildren() {
            if (allowAdjustingHeight) {
                double w = getWidth();
                double h = getHeight();
                layoutInArea(webview,0,0,w,h,0, HPos.CENTER, VPos.CENTER);
            } else {
                super.layoutChildren();
            }
        }

        private void adjustHeight() {
            Platform.runLater(() -> {
                try {
                    Object result = webEngine.executeScript(
                            "var myDiv = document.getElementById('mydiv');" +
                                    "if (myDiv != null) myDiv.offsetHeight");
                    if (result instanceof Integer) {
                        Integer i = (Integer) result;
                        double height = new Double(i);
                        height = height + 20;
                        getStage().setHeight(height);
                        if (height > 1000) {
                            getStage().setWidth(600);
                        }
                    }
                } catch (JSException e) {
                    e.printStackTrace();
                }
            });
        }

        private String getHtml(String content) {
            return "<html><body>" +
                    "<div id=\"mydiv\">" + content + "</div>" +
                    "</body></html>";
        }

    }
}
