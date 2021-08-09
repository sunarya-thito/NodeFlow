package thito.nodeflow.internal.resource;

import thito.nodeflow.api.resource.*;

import java.io.*;

public class InnerResourceDirectoryImpl implements ResourceDirectory {

    private final String name;
    private final String[] paths;
    private final ResourceDirectory parent;
    public InnerResourceDirectoryImpl(String[] paths, String name, ResourceDirectory parent) {
        this.name = name;
        this.paths = paths;
        this.parent = parent;
    }

    @Override
    public Resource[] getChildren() {
        Resource[] resources = new Resource[paths.length];
        for (int i = 0; i < resources.length; i++) {
            resources[i] = ResourceManagerImpl.resourceToResource(paths[i], getClass().getClassLoader().getResourceAsStream(getPath() + File.separator + paths[i]), this);
        }
        return resources;
    }

    @Override
    public Resource getChild(String path) {
        String[] split = path.split(File.separator);
        if (split.length <= 1) {
            return ResourceManagerImpl.resourceToResource(split[0], getClass().getClassLoader().getResourceAsStream(getPath() + File.separator + getName()), this);
        }
        Resource currentChild = this;
        for (int i = 0; i < split.length; i++) {
            if (currentChild instanceof ResourceDirectory) {
                currentChild = ((ResourceDirectory) currentChild).getChild(split[i]);
            } else if (currentChild instanceof UnknownResource) {
                currentChild = ((UnknownResource) currentChild).getChild(split[i]);
            } else {
                return null;
            }
        }
        return currentChild;
    }

    @Override
    public ResourceDirectory getParentDirectory() {
        return parent;
    }

    @Override
    public String getPath() {
        return parent == null ? name : parent.getPath() + File.separator + name;
    }

    @Override
    public String getName() {
        return name;
    }
}
