package thito.nodeflow.java.generated;

import thito.nodeflow.java.*;
import thito.nodeflow.java.generated.body.*;

import java.util.*;
import java.util.function.*;

public class GConstructor extends AbstractConstructor {
    private List<IClass> parameterTypes = new ArrayList<>();
    private List<Annotated> annotatedList = new ArrayList<>();
    private Consumer<ConstructorBodyAccessor> body;
    private List<IClass> throwsClasses = new ArrayList<>();
    public GConstructor(IClass declaringClass) {
        super(declaringClass);
    }

    public GConstructor setModifier(int modifiers) {
        this.modifiers = modifiers;
        return this;
    }

    public List<IClass> getThrowsClasses() {
        return throwsClasses;
    }

    public void setThrows(IClass...throwsClasses) {
        this.throwsClasses = new ArrayList<>(Arrays.asList(throwsClasses));
    }

    @Override
    public IClass[] getThrows() {
        return throwsClasses.toArray(new IClass[0]);
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

    @Override
    public Annotated[] getAnnotations() {
        return annotatedList.toArray(new Annotated[0]);
    }

    public GConstructor annotated(Annotated annotated) {
        annotatedList.add(annotated);
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
