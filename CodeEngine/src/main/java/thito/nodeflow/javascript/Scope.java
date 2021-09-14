package thito.nodeflow.javascript;

import java.util.*;

public abstract class Scope extends Reference {

    protected Scope parentScope;

    private Set<String> variableScope;
    private List<String> lines;
    public String toSourceCode() {
        variableScope = new HashSet<>();
        lines = new ArrayList<>();
        body();
        List<String> sourceCode = lines;
        lines = null;
        variableScope = null;
        return String.join("\n", sourceCode);
    }

    public abstract void body();

    protected Reference letOrSet(String name, Reference value) {
        if (isVariableDefined(name)) {
            return set(name, value);
        }
        return let(name, value);
    }

    protected Reference let(String name) {
        if (!variableScope.add(name)) throw new IllegalArgumentException("already defined in this scope");
        lines.add("let " + name + ";");
        return new Reference() {
            @Override
            public String toSourceCode() {
                return name;
            }
        };
    }

    protected Reference let(String name, Reference value) {
        if (!variableScope.add(name)) throw new IllegalArgumentException("already defined in this scope");
        lines.add("let " + name + " = " + value.toSourceCode() + ";");
        return new Reference() {
            @Override
            public String toSourceCode() {
                return name;
            }
        };
    }

    protected boolean isVariableDefined(String name) {
        Scope current = this;
        while (current != null) {
            if (current.variableScope.contains(name)) return true;
            current = current.parentScope;
        }
        return false;
    }

    protected Reference set(String name, Reference value) {
        if (!isVariableDefined(name)) throw new IllegalArgumentException("no variable found with name "+name);
        lines.add(name + " = " + value.toSourceCode());
        return new Reference() {
            @Override
            public String toSourceCode() {
                return name;
            }
        };
    }

    protected Function function(String...args) {
        return null;
    }

    protected Reference ref(Object obj) {
        return null;
    }

}
