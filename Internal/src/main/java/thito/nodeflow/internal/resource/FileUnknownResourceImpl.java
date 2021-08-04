package thito.nodeflow.internal.resource;

import thito.nodeflow.api.*;
import thito.nodeflow.api.resource.*;

import java.io.*;

public class FileUnknownResourceImpl extends FileResourceFileImpl implements UnknownResource {

    public FileUnknownResourceImpl(File file) {
        super(file);
    }

    @Override
    public Resource[] getChildren() {
        return new Resource[0];
    }

    @Override
    public ResourceFile createFile() {
        try {
            File parentFile = getFile().getParentFile();
            if (parentFile != null) parentFile.mkdirs();
            if (getFile().createNewFile()) {
                return (ResourceFile) ResourceManagerImpl.fileToResource(getFile());
            } else {
                return null;
            }
        } catch (Throwable t) {
            throw new ReportedError(getPath(), t);
        }
    }

    @Override
    public ResourceDirectory createDirectory() {
        try {
            if (getFile().mkdirs()) {
                return (ResourceDirectory) ResourceManagerImpl.fileToResource(getFile());
            } else {
                return null;
            }
        } catch (Throwable t) {
            throw new ReportedError(t);
        }
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
