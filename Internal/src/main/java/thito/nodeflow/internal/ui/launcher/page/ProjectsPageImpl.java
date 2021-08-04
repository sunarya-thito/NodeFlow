package thito.nodeflow.internal.ui.launcher.page;

import javafx.scene.*;
import thito.nodeflow.api.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.project.*;
import thito.nodeflow.api.resource.*;
import thito.nodeflow.api.settings.*;
import thito.nodeflow.api.task.*;
import thito.nodeflow.api.ui.launcher.page.*;
import thito.nodeflow.internal.Toolkit;
import thito.nodeflow.internal.resource.*;
import thito.nodeflow.internal.ui.launcher.*;
import thito.nodeflow.internal.ui.launcher.page.projects.*;

import java.io.*;
import java.util.*;

public class ProjectsPageImpl extends AbstractLauncherPage implements ProjectsPage {

    private List<ProjectProperties> projects = null;

    public ProjectsPageImpl() {
        super(I18n.$("launcher-button-projects"), I18n.$("launcher-page-projects"), null,
                NodeFlow.getApplication().getResourceManager().getIcon("launcher-button-projects"));
        NodeFlow.getApplication().getSettings().get(ApplicationSettings.WORKSPACE_DIRECTORY).impl_valueProperty()
                .addListener(x -> refreshProjects());
        setFooterEnabled(false);
        setHeaderEnabled(false);
    }

    @Override
    public void refreshProjects() {
        projects = null;
        if (peer != null) {
            peer.reloadContent();
            Task.runOnForeground("refresh-project-list", () -> {
                peer.updateShown();
            });
        }
    }

    @Override
    public List<ProjectProperties> getShownProjects() {
        if (projects == null) {
            thito.nodeflow.internal.Toolkit.info("Fetching Projects...");
            projects = new ArrayList<>();
            File directory = NodeFlow.getApplication().getSettings().getValue(ApplicationSettings.WORKSPACE_DIRECTORY);
            if (directory != null) {
                File[] files = directory.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.getName().equals("$RecycleBin")) continue;
                        Toolkit.info("Scanning "+file);
                        file = new File(file, "project.yml");
                        if (file.isFile()) {
                            projects.add(
                                    NodeFlow.getApplication().getProjectManager().loadProjectProperties(
                                            (ResourceFile) ResourceManagerImpl.fileToResource(file)
                                    )
                            );
                        }
                    }
                }
            }
        }
        return projects;
    }

    private ProjectsPagePeer peer;
    @Override
    protected Node requestViewport() {
        refreshProjects();
        if (peer == null) {
            peer = new ProjectsPagePeer(this);
        }
        return peer;
    }
}
