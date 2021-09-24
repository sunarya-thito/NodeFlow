package thito.nodeflow.launcher;

import java.io.*;
import java.net.*;
import java.util.*;

public class Main {

    static ApplicationClassLoader applicationClassLoader;
    public static void main(String[] args) {
        File currentDirectory = new File("").getAbsoluteFile();
        File[] bins = new File(currentDirectory, "Libraries").listFiles();
        if (bins == null) {
            bins = new File[0];
        }
        URL[] urls = Arrays.stream(bins).filter(f -> f.getName().endsWith(".jar")).map(Main::toURL).toArray(URL[]::new);
        for (URL u : urls) System.out.println("Loading "+u);
        applicationClassLoader = new ApplicationClassLoader(urls, Main.class.getClassLoader());
        try {
            Thread.currentThread().setContextClassLoader(applicationClassLoader);
            applicationClassLoader.initialize(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static URL toURL(File f) {
        try {
            return f.toURI().toURL();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

}
