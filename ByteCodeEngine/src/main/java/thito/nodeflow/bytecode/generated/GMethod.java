package thito.nodeflow.bytecode.generated;

import thito.nodeflow.bytecode.*;
import thito.nodeflow.bytecode.generated.body.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

public class GMethod extends AbstractMethod {
    private List<Annotated> annotatedList = new ArrayList<>();
    private List<IClass> parameterTypes = new ArrayList<>();
    private IClass returnType = Java.Class(void.class);
    private Consumer<MethodBodyAccessor> body;
    public GMethod(String name, IClass declaringClass) {
        super(name, declaringClass);
    }

    public Consumer<MethodBodyAccessor> getBody() {
        return body;
    }

    public GMethod setBody(Consumer<MethodBodyAccessor> body) {
        this.body = body;
        return this;
    }

    public GMethod setModifier(int modifier) {
        this.modifiers = modifier;
        return this;
    }

    public GMethod setReturnType(IClass returnType) {
        this.returnType = returnType;
        return this;
    }

    public GMethod setParameterTypes(IClass...types) {
        parameterTypes.clear();
        parameterTypes.addAll(Arrays.asList(types));
        return this;
    }

    public GMethod annotated(String name, Object value) {
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
    public IClass getReturnType() {
        return returnType;
    }

    @Override
    public IClass[] getParameterTypes() {
        return parameterTypes.toArray(new IClass[0]);
    }
}
