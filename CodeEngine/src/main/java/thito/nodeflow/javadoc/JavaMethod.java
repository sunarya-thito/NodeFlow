package thito.nodeflow.javadoc;

import java.io.*;

public class JavaMethod extends JavaMember {

    {
        memberType = "JavaMethod";
    }

    private TypeReference returnType;
    private TypeReference[] throwsClasses;
    private Parameter[] parameters;
    private TypeReference[] genericParameters;
    private boolean isDefaultMethod;

    public boolean isDefaultMethod() {
        return isDefaultMethod;
    }

    public void setDefaultMethod(boolean defaultMethod) {
        isDefaultMethod = defaultMethod;
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
        private String name;
        private boolean varargs;

        public JavaAnnotation[] getAnnotations() {
            return annotations;
        }

        public void setAnnotations(JavaAnnotation[] annotations) {
            this.annotations = annotations;
        }

        public boolean isVarargs() {
            return varargs;
        }

        public void setVarargs(boolean varargs) {
            this.varargs = varargs;
        }

        public String getName() {
            return name;
        }

        public TypeReference getType() {
            return type;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setType(TypeReference type) {
            this.type = type;
        }

        public String toString() {
            return type + " " + name;
        }
    }
}
