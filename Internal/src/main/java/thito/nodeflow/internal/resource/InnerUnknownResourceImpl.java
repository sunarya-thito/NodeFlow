package thito.nodeflow.internal.resource;

import thito.nodeflow.api.*;
import thito.nodeflow.api.resource.*;

import java.io.*;

public class InnerUnknownResourceImpl implements UnknownResource {
    private final String fileName;
    private final ResourceDirectory parent;

    public InnerUnknownResourceImpl(String fileName, ResourceDirectory parent) {
        this.fileName = fileName;
        this.parent = parent;
    }

    @Override
    public Resource[] getChildren() {
        return new Resource[0];
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public ResourceDirectory getParentDirectory() {
        return parent;
    }

    @Override
    public String getPath() {
        return parent != null ? parent.getPath() + File.separator + getFileName() : getFileName();
    }

    @Override
    public String getName() {
        String fileName = getFileName();
        int index = fileName.lastIndexOf('.');
        return index >= 0 ? fileName.substring(0, index) : fileName;
    }

    @Override
    public ResourceFile createFile() {
        throw new ReportedError(new UnsupportedOperationException());
    }

    @Override
    public ResourceDirectory createDirectory() {
        throw new ReportedError(new UnsupportedOperationException());
    }

    @Override
    public Resource getChild(String path) {
        return new InnerUnknownResourceImpl(fileName, getParentDirectory());
    }
}
