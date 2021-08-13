import javafx.scene.Node;
import org.jsoup.*;
import org.jsoup.nodes.*;

import java.net.*;

public class ClassFetch {
    public static void main(String[] args) throws Throwable {
        Document doc = Jsoup.parse(new URL("https://openjfx.io/javadoc/11/allclasses-index.html"), 10000);
        for (Element e : doc.select("body > main > div.allClassesContainer > ul > li > table > tbody > tr")) {
            if (e.hasAttr("id") && e.id().startsWith("i")) {
                Element el = e.selectFirst("a");
                if (el.hasAttr("href")) {
                    String clz = el.attr("href");
                    clz = clz.substring(clz.indexOf('/') + 1);
                    clz = clz.replace('.', '$').replace('/', '.').replace("$html", "");
                    try {
                        Class<?> test = Class.forName(clz, false, ClassFetch.class.getClassLoader());
                        test.getConstructor();
                        if (Node.class.isAssignableFrom(test)) {
                            System.out.println("\"" + test.getName() + "\", ");
                        }
                    } catch (Throwable t) {
                    }
                }
            }
        }
    }
}
