package thito.nodeflow.internal.ui.launcher;

import javafx.stage.*;
import thito.nodeflow.api.*;
import thito.nodeflow.api.ui.*;
import thito.nodeflow.api.ui.launcher.*;
import thito.nodeflow.api.ui.menu.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.internal.ui.*;
import thito.nodeflow.internal.ui.launcher.page.*;

import java.util.*;

public class LauncherWindowImpl extends WindowImpl implements LauncherWindow, ConfirmationClose {
    private List<LauncherPage> pages = new ArrayList<>();

    public LauncherWindowImpl() {
        Stage stage = impl_getPeer();
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        initializeDefaultPages();
    }

    @Override
    public String getName() {
        return "Launcher";
    }

    @Override
    protected void initializeViewport() {
        setViewport(new LauncherUI(this));
    }

    @Override
    public void show() {
        super.show();
    }

    private ProjectsPageImpl projectsPage;

    private void initializeDefaultPages() {
        pages.add(new UpdatesPageImpl());
        pages.add(projectsPage = new ProjectsPageImpl());
//        pages.add(new SettingsPageImpl());
//        pages.add(new AboutPageImpl());

        // Menus
        Menu menu = getMenu();

        menu.getItems().add(requestDefaultApplicationMenu());
        menu.getItems().add(requestDefaultWindowMenu());
        menu.getItems().add(requestDefaultHelpMenu());
    }

    public ProjectsPageImpl getProjectsPage() {
        return projectsPage;
    }

    @Override
    public List<LauncherPage> getPages() {
        return pages;
    }

    @Override
    public boolean askFirstBeforeClosing() {
        return NodeFlow.getApplication().getProjectManager().getLoadedProjects().length <= 0;
    }
}
