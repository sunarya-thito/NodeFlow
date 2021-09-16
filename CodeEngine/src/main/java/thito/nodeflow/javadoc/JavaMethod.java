package thito.nodeflow.javadoc;

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
        private LocalFieldDeclaration name;
        private Boolean varargs;

        public JavaAnnotation[] getAnnotations() {
            return annotations;
        }

        public void setAnnotations(JavaAnnotation[] annotations) {
            this.annotations = annotations != null && annotations.length == 0 ? null : annotations;
        }

        public boolean isVarargs() {
            return varargs != null && varargs;
        }

        public void setVarargs(boolean varargs) {
            this.varargs = varargs ? true : null;
        }

        public void setName(LocalFieldDeclaration name) {
            this.name = name;
        }

        public TypeReference getType() {
            return type;
        }

        public LocalFieldDeclaration getName() {
            return name;
        }

        public void setType(TypeReference type) {
            this.type = type;
        }

        public String toString() {
            return type + " " + name;
        }
    }
}
