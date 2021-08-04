package thito.nodeflow.internal.resource;

import thito.nodeflow.api.resource.*;

import java.io.*;

public class FileResourceDirectoryImpl extends FileResourceImpl implements ResourceDirectory {
    public FileResourceDirectoryImpl(File file) {
        super(file);
    }

    @Override
    public Resource[] getChildren() {
        File[] listFiles = getFile().listFiles();
        if (listFiles != null) {
            Resource[] resources = new Resource[listFiles.length];
            for (int i = 0; i < resources.length; i++) {
                resources[i] = ResourceManagerImpl.fileToResource(listFiles[i]);
            }
            return resources;
        }
        return new Resource[0];
    }

    @Override
    public Resource getChild(String path) {
        String[] split = path.replace(File.separator, "/").split("/");
        if (split.length <= 1) {
            File sub = new File(getFile(), path);
            return ResourceManagerImpl.fileToResource(sub);
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

}
