package thito.nodeflow.java;

import java.util.*;

public class SourceCode implements AutoCloseable {
    private static ThreadLocal<SourceCode> sourceCodeThreadLocal = new ThreadLocal<>();

    public static SourceCode getContext() {
        SourceCode code = sourceCodeThreadLocal.get();
        if (code == null) throw new IllegalStateException("no context");
        return code;
    }

    public static boolean hasContext() {
        return sourceCodeThreadLocal.get() != null;
    }

    protected static SourceCode openContext() {
        if (hasContext()) throw new IllegalStateException("already opened");
        SourceCode code = new SourceCode();
        sourceCodeThreadLocal.set(code);
        return code;
    }

    private Map<String, IClass> importMap = new HashMap<>();

    private static final String indention = "    ";
    private Set<Integer> variables = new HashSet<>();
    private List<StringBuilder> lines = new ArrayList<>();
    private StringBuilder line;
    private int tabIndent;

    public Map<String, IClass> getImportMap() {
        return importMap;
    }

    public void setTabIndent(int tabIndent) {
        this.tabIndent = tabIndent;
    }

    public void incIndent() {
        tabIndent++;
    }

    public int getTabIndent() {
        return tabIndent;
    }

    public void decIndent() {
        tabIndent--;
    }

    public StringBuilder getLine() {
        return line == null ? line = new StringBuilder() : line;
    }

    public Set<Integer> getVariables() {
        return variables;
    }

    public String generalizeType(IClass type) {
        if (type.getName().equals("void")) return "void";
        if (BCHelper.isPrimitive(type)) return type.getName();
        IClass other = importMap.get(type.getSimpleName());
        if (other != null) {
            if (type.getName().equals(other.getName())) return type.getSimpleName();
            return type.getName();
        }
        importMap.put(type.getSimpleName(), type);
        return type.getSimpleName();
    }

    public List<StringBuilder> getLines() {
        return lines;
    }

    public void endLine() {
        for (int i = 0; i < tabIndent; i++) getLine().insert(0, indention);
        lines.add(getLine());
        line = null;
    }

    @Override
    public void close() {
        if (sourceCodeThreadLocal.get() != this) throw new IllegalStateException("already closed");
        sourceCodeThreadLocal.set(null);
    }
}
