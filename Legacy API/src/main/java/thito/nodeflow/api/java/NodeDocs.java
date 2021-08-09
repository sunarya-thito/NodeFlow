package thito.nodeflow.api.java;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
public @interface NodeDocs {
    String value();
    /*

    @NodeCode("Changes input text to number")
    @NodeArgument("Parsed Number")
    public int test(@NodeArgument("Text") String numberString) {
        return Integer.parseInt(numberString);
    }

    Node Arguments:
        1. [Input: String] [Output: NONE]
        2. [Input: NONE] [Output: Integer]

     */
}
