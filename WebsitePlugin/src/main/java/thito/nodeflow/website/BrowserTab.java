package thito.nodeflow.website;

import com.codebrig.journey.*;
import javafx.embed.swing.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import thito.nodeflow.internal.language.*;
import thito.nodeflow.internal.ui.editor.*;

import javax.swing.*;
import java.awt.*;

public class BrowserTab extends EditorTab {

    private Tab tab;

    // Layout
    private BorderPane root = new BorderPane();
    private JourneyBrowserView browserView;
    // End Layout

    public BrowserTab() {
        browserView = new JourneyBrowserView();
        tab = new Tab();
        tab.textProperty().bind(I18n.$("plugin.browser"));
        SwingNode node = new SwingNode();
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(browserView, BorderLayout.CENTER);
        node.setContent(panel);
        root.setCenter(node);
    }

    public JourneyBrowserView getBrowserView() {
        return browserView;
    }

    @Override
    public Tab getTab() {
        return tab;
    }
}
