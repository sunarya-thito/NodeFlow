package thito.nodeflow.project;

public interface ProjectHandlerFactory {
    ProjectHandler createHandler(ProjectContext context);
}
