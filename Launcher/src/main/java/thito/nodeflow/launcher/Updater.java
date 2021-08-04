package thito.nodeflow.launcher;

import com.google.gson.*;
import javafx.stage.*;

import javax.xml.bind.annotation.adapters.*;
import java.io.*;
import java.net.*;
import java.security.*;
import java.util.*;

public class Updater extends Stage {

    public static final String BASE_URL =
            "https://api.github.com/repos/sunarya-thito/NodeFlow/git/trees/%s?recursive=1";

    public static final Gson GSON = new Gson();

    public static List<String> IGNORED_FILES = Arrays.asList(
            "NodeFlow.exe", "NodeFlow-CLI.exe", "NodeFlow-CLI-Direct.exe", "NodeFlow-Direct.exe", "dump.tmp", ".gitignore", "NodeFlow-Beta.exe", "config.yml",
            "NodeFlow", "user.yml"
    );

    public static List<Resource> filterResources(Branch branch) throws IOException, NoSuchAlgorithmException {
        List<Resource> resources = new ArrayList<>();
        for (Resource resource : branch.tree) {
            if (IGNORED_FILES.contains(resource.path) || resource.type.equalsIgnoreCase("tree")) continue;
            if (resource.path.startsWith("docs") || resource.path.startsWith(".github")) continue;
            File target = new File(resource.path);
            if (target.exists()) {
                String sha = calcSHA1(target);
                if (!sha.equals(resource.sha)) {
                    System.out.println("Found mismatch sha: "+resource.path+" "+resource.sha+" => "+sha);
                    resources.add(resource);
                }
            } else {
                System.out.println("Found missing file: "+resource.path);
                resources.add(resource);
            }
        }
        return resources;
    }

    private static String calcSHA1(File file) throws
            IOException, NoSuchAlgorithmException {

        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        try (InputStream input = new FileInputStream(file)) {

            sha1.update(("blob "+file.length()+"\0").getBytes());

            byte[] buffer = new byte[1024 * 16];
            int len;

            while ((len = input.read(buffer, 0, buffer.length)) != -1) {
                sha1.update(buffer, 0, len);
            }

            return new HexBinaryAdapter().marshal(sha1.digest()).toLowerCase();
        }
    }

    public static Branch getBranch(String name) throws Throwable {
        URL url = new URL(String.format(BASE_URL, name));
        try (InputStreamReader reader = new InputStreamReader(url.openStream())) {
            Branch branch = GSON.fromJson(reader, Branch.class);
            for (Resource resource : branch.tree) {
                resource.branch = name;
            }
            return branch;
        } catch (Throwable t) {
            throw new Error("Failed to connect", t);
        }
    }

    public static class Branch {
        public String sha;
        public String url;
        public Resource[] tree;
    }

    public static class Resource {
        public String path;
        public String mode;
        public String type;
        public String sha;
        public long size;
        public String branch;
        @Deprecated // not the download URL
        public String url;

        public String getDownloadURL() throws Throwable {
            URL url= new URL("https://raw.githubusercontent.com/sunarya-thito/NodeFlow/"+branch+"/"+path);
            URI uri = new URI(url.getProtocol(), url.getUserInfo(), IDN.toASCII(url.getHost()), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
            return uri.toASCIIString();
        }

        @Override
        public String toString() {
            return "Resource{" +
                    "path='" + path + '\'' +
                    ", mode='" + mode + '\'' +
                    ", type='" + type + '\'' +
                    ", sha='" + sha + '\'' +
                    ", size=" + size +
                    ", branch='" + branch + '\'' +
                    ", url='" + url + '\'' +
                    '}';
        }
    }
}
