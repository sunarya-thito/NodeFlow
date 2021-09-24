package thito.nodeflow.website;

import javafx.beans.property.*;
import javafx.embed.swing.*;
import javafx.scene.layout.*;
import org.cef.*;
import org.cef.browser.*;
import org.cef.handler.*;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class ChromeBrowser extends StackPane {
    public static void initialize() throws IOException {
        JCefLoader.installAndLoadCef();
        CefApp.addAppHandler(new CefAppHandlerAdapter(null) {
            @Override
            public void stateHasChanged(CefApp.CefAppState state) {
                if (state == CefApp.CefAppState.TERMINATED) {
                    // CRASH IT OUT
                    System.exit(0);
                }
            }
        });
    }

    private CefApp app;
    private CefClient client;
    private ObjectProperty<CefBrowser> browser = new SimpleObjectProperty();
    private SwingNode swingNode = new SwingNode();

    public ChromeBrowser() {
        CefSettings settings = new CefSettings();
        app = CefApp.getInstance(settings);
        client = app.createClient();

        getChildren().add(swingNode);

        browser.addListener((obs, old, val) -> {
            if (old != null) {
                old.doClose();
            }
            swingNode.setContent(null);
            if (val != null) {
                JPanel panel = new JPanel();
                panel.add(val.getUIComponent(), BorderLayout.CENTER);
                swingNode.setContent(panel);
            }
        });
    }

    public CefBrowser getBrowser() {
        return browser.get();
    }

    public void changeBrowser(File url) {
        try {
            changeBrowser(url.toURI().toURL().toExternalForm());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void changeBrowser(String url) {
        browser.set(client.createBrowser(url, true, false));
    }
}
