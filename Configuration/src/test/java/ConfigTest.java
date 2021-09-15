import thito.nodeflow.config.*;

public class ConfigTest {
    public static void main(String[] args) {
        Section section = new MapSection();
        section.set("test.test2.test3", "testificate");
        System.out.println(Section.toString(section));
    }
}
