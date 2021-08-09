package thito.nodeflow.internal;

import javafx.scene.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.media.*;
import javafx.stage.*;
import thito.nodeflow.api.*;
import thito.nodeflow.api.resource.*;
import thito.nodeflow.api.ui.*;

import java.net.*;

public class IntroPlayer extends Stage implements ExceptionalStage {

    private static String requestVideo() {
        return ((PhysicalResource) NodeFlow.getApplication().getResourceManager().getResource("videos/Intro.mp4")).getSystemPath().toUri().toString();
    }
    private MediaPlayer player = new MediaPlayer(new Media(requestVideo()));
    private MediaView view = new MediaView(player);
    private BorderPane root = new BorderPane(view);
    private Scene scene = new Scene(root);

    public IntroPlayer(Runnable done) throws URISyntaxException {
        Icon icon = NodeFlow.getApplication().getResourceManager().getIcon("favicon");
        icon.impl_propertyPeer().addListener((obs, old, val) -> {
            if (val != null) {
                getIcons().setAll(val);
            }
        });
        getIcons().setAll(icon.impl_propertyPeer().get());
        view.fitWidthProperty().bind(root.widthProperty());
        view.fitHeightProperty().bind(root.heightProperty());
        setScene(scene);
        setFullScreen(true);
        setFullScreenExitHint(null);
        setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        view.setOnMouseClicked(event -> player.stop());
        player.onStoppedProperty().set(() -> {
            close();
            done.run();
        });
        player.setOnEndOfMedia(player.onStoppedProperty().get());
        setAlwaysOnTop(true);
        show();
        toFront();
        player.play();
    }

}
