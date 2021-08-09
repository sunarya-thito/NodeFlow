package thito.nodeflow.library;

import java.util.*;
import java.util.stream.*;

public class Group {
    protected char open, close;
    protected JavaTokenizer[] tokenizers;

    public Group(char open, char close, JavaTokenizer[] tokenizers) {
        this.tokenizers = tokenizers;
        this.open = open;
        this.close = close;
    }

    public JavaTokenizer[] getMembers() {
        return tokenizers;
    }

    public char getOpen() {
        return open;
    }

    public char getClose() {
        return close;
    }

    public String toString() {
        return open + Arrays.stream(tokenizers).map(JavaTokenizer::toString).collect(Collectors.joining(", ")) + close;
    }
}
