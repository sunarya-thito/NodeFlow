package thito.nodeflow.library.ui;

import javafx.scene.Node;
import org.jsoup.nodes.*;

public interface SkinHandler<T extends Node> {
    void parse(SkinParser parser, T node, Element element);
}
