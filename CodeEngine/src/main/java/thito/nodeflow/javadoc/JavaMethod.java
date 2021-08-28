package thito.nodeflow.javadoc;

public class JavaMethod extends JavaMember {

    private String returnType;
    private String[] throwsClasses;
    private Parameter[] parameters;

    public boolean isConstructor() {
        return getName().equals("<init>");
    }

    public void setThrowsClasses(String[] throwsClasses) {
        this.throwsClasses = throwsClasses;
    }

    public String[] getThrowsClasses() {
        return throwsClasses;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public void setParameters(Parameter[] parameters) {
        this.parameters = parameters;
    }

    public Parameter[] getParameters() {
        return parameters;
    }

    public static class Parameter {
        private String type;
        private String name;

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
