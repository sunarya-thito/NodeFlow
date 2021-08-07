package thito.nodeflow.bytecode.generated;

import thito.nodeflow.bytecode.*;
import thito.nodeflow.bytecode.generated.body.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

public class GConstructor extends AbstractConstructor {
    private List<IClass> parameterTypes = new ArrayList<>();
    private List<Annotated> annotatedList = new ArrayList<>();
    private Consumer<ConstructorBodyAccessor> body;
    public GConstructor(IClass declaringClass) {
        super(declaringClass);
    }

    public GConstructor setModifier(int modifiers) {
        this.modifiers = modifiers;
        return this;
    }

    public GConstructor setBody(Consumer<ConstructorBodyAccessor> bodyAccessor) {
        this.body = bodyAccessor;
        return this;
    }

    public Consumer<ConstructorBodyAccessor> getBody() {
        return body;
    }

    public GConstructor setParameterTypes(IClass... types) {
        parameterTypes.clear();
        parameterTypes.addAll(Arrays.asList(types));
        return this;
    }

    public GConstructor annotated(String name, Object value) {
        annotatedList.add(new Annotated(name, value));
        return this;
    }

    public List<Annotated> getAnnotatedList() {
        return annotatedList;
    }

    public List<IClass> getParameterTypeList() {
        return parameterTypes;
    }

    @Override
    public int getParameterCount() {
        return parameterTypes.size();
    }

    @Override
    public IClass[] getParameterTypes() {
        return parameterTypes.toArray(new IClass[0]);
    }
}
