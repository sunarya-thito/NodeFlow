import thito.nodeflow.javadoc.tokenizer.*;

import java.util.*;

public class TokenizerTest {
    public static void main(String ... args) {
        TypeTokenizer typeTokenizer = new TypeTokenizer(0, "java.lang.Object@NonNull[]test".toCharArray());
        Object x = typeTokenizer.eatParameter();
        System.out.println(x);
        System.out.println(Map.Entry.class.getSimpleName());
    }
}
