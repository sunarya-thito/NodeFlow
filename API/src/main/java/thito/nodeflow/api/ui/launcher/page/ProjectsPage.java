package thito.nodeflow.api.ui.launcher.page;

import thito.nodeflow.api.project.ProjectProperties;
import thito.nodeflow.api.ui.launcher.LauncherPage;

import java.util.List;

public interface ProjectsPage extends LauncherPage {

    void refreshProjects();

    List<ProjectProperties> getShownProjects();

}
