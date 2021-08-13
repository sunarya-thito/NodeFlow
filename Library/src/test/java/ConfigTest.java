import thito.nodeflow.library.config.*;

import java.util.*;

public class ConfigTest {
    public static void main(String[] args) {
        Section section = new MapSection();
        section.set("test.nothing.weirder", "test123");
        section.set("test.nothing.same", "a1231");
        section.set("test.something", "lmao");
        section.set("test.nothing.same", "123");
        System.out.println(section);
        ArrayList<String> a = new ArrayList<>();
        a.add("a");
        a.add("a");
        a.remove("a");
        System.out.println(a);
    }
}
