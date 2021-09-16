package thito.nodeflow.javadoc.element;

import thito.nodeflow.javadoc.element.declaration.*;
import thito.nodeflow.javadoc.element.reference.*;

public class JavaMethod extends JavaMember {

    {
        memberType = "JavaMethod";
    }

    private TypeReference returnType;
    private TypeReference[] throwsClasses;
    private Parameter[] parameters;
    private TypeReference[] genericParameters;
    private Boolean isDefaultMethod;

    public boolean isDefaultMethod() {
        return isDefaultMethod != null && isDefaultMethod;
    }

    public void setDefaultMethod(boolean defaultMethod) {
        isDefaultMethod = defaultMethod ? true : null;
    }

    public TypeReference[] getGenericParameters() {
        return genericParameters;
    }

    public void setGenericParameters(TypeReference[] genericParameters) {
        this.genericParameters = genericParameters;
    }

    public boolean isConstructor() {
        return getName().equals("<init>");
    }

    public void setThrowsClasses(TypeReference[] throwsClasses) {
        this.throwsClasses = throwsClasses;
    }

    public TypeReference[] getThrowsClasses() {
        return throwsClasses;
    }

    public TypeReference getReturnType() {
        return returnType;
    }

    public void setReturnType(TypeReference returnType) {
        this.returnType = returnType;
    }

    public void setParameters(Parameter[] parameters) {
        this.parameters = parameters;
    }

    public Parameter[] getParameters() {
        return parameters;
    }

    public static class Parameter {
        private JavaAnnotation[] annotations;
        private TypeReference type;
        private LocalFieldDeclaration field;
        private ArrayDimensionDeclaration varArgs;

        public JavaAnnotation[] getAnnotations() {
            return annotations;
        }

        public void setAnnotations(JavaAnnotation[] annotations) {
            this.annotations = annotations != null && annotations.length == 0 ? null : annotations;
        }

        public ArrayDimensionDeclaration getVarArgs() {
            return varArgs;
        }

        public void setVarArgs(ArrayDimensionDeclaration varArgs) {
            this.varArgs = varArgs;
        }

        public void setField(LocalFieldDeclaration field) {
            this.field = field;
        }

        public TypeReference getType() {
            return type;
        }

        public LocalFieldDeclaration getField() {
            return field;
        }

        public void setType(TypeReference type) {
            this.type = type;
        }

        public String toString() {
            return type + " " + field;
        }
    }
}
