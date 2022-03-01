package thito.nodeflow.plugin.base;

import thito.nodeflow.annotation.BGThread;
import thito.nodeflow.project.Project;
import thito.nodeflow.project.ProjectContext;
import thito.nodeflow.task.TaskThread;
import thito.nodeflow.task.batch.Batch;

import java.io.File;
import java.util.*;

public class LibraryContext {
    private final Map<Library, List<Project>> libraryMap = new HashMap<>();
    private final Set<LibraryLoader> loaderSet = new HashSet<>();
    @BGThread
    public void registerLoader(LibraryLoader loader) {
        loaderSet.add(loader);
    }
    @BGThread
    public void unregisterLoader(LibraryLoader loader) {
        loaderSet.remove(loader);
    }
    @BGThread
    public Set<Library> getLibraryList(Project project) {
        Set<Library> librarySet = new HashSet<>();
        libraryMap.forEach((library, projectList) -> {
            if (projectList.contains(project)) {
                librarySet.add(library);
            }
        });
        return librarySet;
    }
    @BGThread
    public void addLibrary(Project project, Library library) {
        List<Project> projects = libraryMap.computeIfAbsent(library, lib -> new ArrayList<>());
        projects.add(project);
        // TODO refresh Blueprints
    }
    @BGThread
    public void removeLibrary(Project project, Library library) {
        List<Project> projects = libraryMap.get(library);
        if (projects != null) {
            projects.remove(project);
            if (projects.isEmpty()) {
                libraryMap.remove(library);
            }
        }
        // TODO refresh blueprints
    }
    @BGThread
    public Batch.Task loadLibraries(Project project) {
        File[] listFiles = new File(project.getDirectory().toFile(), "lib").listFiles();
        Batch.Task task = null;
        if (listFiles != null) {
            for (File file : listFiles) {
                Batch.Task t = loadLibrary(project, file);
                if (task == null) {
                    task = t;
                } else {
                    task.execute(t);
                }
            }
        }
        return task;
    }
    @BGThread
    public Batch.Task loadLibrary(Project project, File f) {
        for (LibraryLoader loader : loaderSet) {
            if (loader.accept(f)) {
                String hash = loader.getHashCode(f);
                Library library = libraryMap.keySet().stream().filter(x -> x.getHashCode().equals(hash)).findAny().orElse(null);
                if (library == null) {
                    return loader.loadLibrary(f, lib -> {
                        TaskThread.BG().schedule(() -> {
                            addLibrary(project, lib);
                        });
                    });
                }
            }
        }
        return null;
    }
}
